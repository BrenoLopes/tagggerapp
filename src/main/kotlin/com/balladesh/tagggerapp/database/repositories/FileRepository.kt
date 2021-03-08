package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.DatabaseConnection
import com.balladesh.tagggerapp.database.entities.File
import com.balladesh.tagggerapp.database.entities.Tag
import com.balladesh.tagggerapp.database.page.*
import com.balladesh.tagggerapp.database.session.DatabaseSessionBuilder
import java.util.Optional
import javax.persistence.EntityExistsException
import javax.persistence.EntityTransaction
import javax.persistence.NoResultException

class FileRepository(private val responseFactory: PagedResponseFactory<File> = DefaultResponseFactory()): PageableRepository<File> {
  private var databaseSessionBuilder: DatabaseSessionBuilder

  init {
    this.databaseSessionBuilder = DatabaseSessionBuilder(DatabaseConnection().persistenceUnitName)
  }

  constructor(sessionBuilder: DatabaseSessionBuilder) : this() {
    this.databaseSessionBuilder = sessionBuilder
  }

  /**
   * Select all files present inside the database
   *
   * @param pageable optional param to determine the amount to be fetched from the database
   *
   * @return List of files in the database
   */
  override fun findAll(pageable: Pageable): PagedResponse<File> {
    val session = this.databaseSessionBuilder.build()

    return try {
      val fetchQuery = session.manager.createQuery("From File f ORDER BY f.name ${pageable.sort}")
      val countQuery = session.manager.createQuery("SELECT COUNT(f.name) FROM File f")

      this.responseFactory.create(session, fetchQuery, countQuery, pageable)
    } catch (e: NoResultException) {
      PagedResponse(pageable.page, pageable.maxSize, 0, emptyList())
    } catch (e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Find a file by it's given id.
   *
   * @param id the id to be searched
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  override fun findById(id: Long): Optional<File> {
    val session = this.databaseSessionBuilder.build()

    try {
      val file = session.manager.find(File::class.java, id)

      return Optional.ofNullable(file)
    } catch(e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Find a file by it's name
   *
   * @param name the file's name to be searched
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  override fun findByName(name: String, pageable: Pageable): PagedResponse<File> {
    val session = this.databaseSessionBuilder.build()

    try {
      val countQuery = session.manager.createQuery("SELECT COUNT(f.name) FROM File f")
      val fetchQuery = session.manager.createQuery("FROM File f WHERE f.name = :name ORDER BY f.name ${pageable.sort}")
      fetchQuery.setParameter("name", name)

      return this.responseFactory.create(session, fetchQuery, countQuery, pageable)
    } catch (e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Find a file by it's association with a tag
   *
   * @param tag the tag to be used in the search
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  fun findByTag(tag: Tag, pageable: Pageable = Pageable()): PagedResponse<File> {
    val session = this.databaseSessionBuilder.build()

    try {
      val countQuery = session.manager.createQuery("SELECT COUNT(f.name) FROM File f")
      val fetchQuery = session.manager.createQuery("SELECT f FROM File f, IN(f.tags) t WHERE t = :tag ORDER BY f.name ${pageable.sort}")
      fetchQuery.setParameter("tag", tag)

      return this.responseFactory.create(session, fetchQuery, countQuery, pageable)
    } catch (e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  override fun persistEntities(vararg entities: File) {
    val databaseSession = this.databaseSessionBuilder.build()
    val transaction = databaseSession.manager.transaction

    try {
      transaction.begin()
      entities.forEach { databaseSession.manager.persist(it) }
      transaction.commit()
    } catch (e: EntityExistsException) {
      this.rollbackTransactionIfActive(transaction)
      throw e
    } catch(e: Exception) {
      this.rollbackTransactionIfActive(transaction)
      throw RuntimeException("Could not persist the file on the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  /**
   * Updates a file in the database
   *
   * @param entities the files to be updated
   *
   * @return RuntimeException if an error happened at runtime
   */
  override fun updateEntities(vararg entities: File) {
    val databaseSession = this.databaseSessionBuilder.build()
    val transaction = databaseSession.manager.transaction

    try {
      transaction.begin()
      entities.forEach { databaseSession.manager.merge(it) }
      transaction.commit()
    } catch (e: Exception) {
      this.rollbackTransactionIfActive(transaction)
      throw RuntimeException("Could not update the file on the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  /**
   * Remove a file from the database
   *
   * @param entities the files to be removed
   *
   * @throws RuntimeException if an error happens at runtime
   */
  override fun removeEntities(vararg entities: File) {
    val databaseSession = this.databaseSessionBuilder.build()
    val transaction = databaseSession.manager.transaction

    try {
      transaction.begin()
      entities.forEach {
        databaseSession.manager.remove(
          databaseSession.manager.find(File::class.java, it.id)
        )
      }
      transaction.commit()
    } catch(e: Exception) {
      this.rollbackTransactionIfActive(transaction)
      throw RuntimeException("Could not remove the file from the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  private fun rollbackTransactionIfActive(transaction: EntityTransaction) {
    if (!transaction.isActive) return

    transaction.rollback()
  }
}
