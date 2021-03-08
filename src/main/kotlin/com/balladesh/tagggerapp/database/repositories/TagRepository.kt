package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.DatabaseConnection
import com.balladesh.tagggerapp.database.entities.File
import com.balladesh.tagggerapp.database.entities.Tag
import com.balladesh.tagggerapp.database.page.DefaultResponseFactory
import com.balladesh.tagggerapp.database.page.Pageable
import com.balladesh.tagggerapp.database.page.PagedResponse
import com.balladesh.tagggerapp.database.page.PagedResponseFactory
import com.balladesh.tagggerapp.database.session.DatabaseSessionBuilder
import java.util.*
import javax.persistence.EntityExistsException
import javax.persistence.EntityTransaction
import javax.persistence.NoResultException

class TagRepository(private val responseFactory: PagedResponseFactory<Tag> = DefaultResponseFactory()): PageableRepository<Tag> {
  private var databaseSessionBuilder: DatabaseSessionBuilder

  init {
    this.databaseSessionBuilder = DatabaseSessionBuilder(DatabaseConnection().persistenceUnitName)
  }

  constructor(sessionBuilder: DatabaseSessionBuilder) : this() {
    this.databaseSessionBuilder = sessionBuilder
  }

  /**
   * Select all tags present inside the database
   *
   * @param pageable optional param to determine the amount to be fetched from the database
   *
   * @return List of tags in the database
   */
  override fun findAll(pageable: Pageable): PagedResponse<Tag> {
    val session = this.databaseSessionBuilder.build()

    return try {
      val fetchQuery = session.manager.createQuery("From Tag t ORDER BY t.name ${pageable.sort}")
      val countQuery = session.manager.createQuery("SELECT COUNT(t.name) FROM Tag t")

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
   * Find a tag by it's given id.
   *
   * @param id the id to be searched
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  override fun findById(id: Long): Optional<Tag> {
    val session = this.databaseSessionBuilder.build()

    try {
      val tag = session.manager.find(Tag::class.java, id)

      return Optional.ofNullable(tag)
    } catch(e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Find a tag by it's given name
   *
   * @param name the file's name to be searched
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  override fun findByName(name: String, pageable: Pageable): PagedResponse<Tag> {
    val session = this.databaseSessionBuilder.build()

    try {
      val countQuery = session.manager.createQuery("SELECT COUNT(t.name) FROM Tag t")
      val fetchQuery = session.manager.createQuery("FROM Tag t WHERE t.name = :name ORDER BY t.name ${pageable.sort}")
      fetchQuery.setParameter("name", name)

      return this.responseFactory.create(session, fetchQuery, countQuery, pageable)
    } catch (e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Find a tag by it's association with a file
   *
   * @param file the file to be used in the search
   *
   * @throws IllegalStateException if the entity manager couldn't be initialized
   * @throws RuntimeException if an error happened during the search
   */
  fun findByFile(file: File, pageable: Pageable = Pageable()): PagedResponse<Tag> {
    val session = this.databaseSessionBuilder.build()

    try {
      val countQuery = session.manager.createQuery("SELECT COUNT(t.name) FROM Tag t")
      val fetchQuery = session.manager.createQuery("SELECT t FROM Tag t, IN (t.files) f WHERE f = :file ORDER BY t.name ${pageable.sort}")
      fetchQuery.setParameter("file", file)

      return this.responseFactory.create(session, fetchQuery, countQuery, pageable)
    } catch (e: Exception) {
      throw RuntimeException("Could not query the database: ${e.message}")
    } finally {
      session.close()
    }
  }

  /**
   * Persist a tag in the database
   *
   * @param entities the tags to be used in the search
   *
   * @throws EntityExistsException if the entity already exists
   * @throws RuntimeException if an error happened during the persistence
   */
  override fun persistEntities(vararg entities: Tag) {
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
      throw RuntimeException("Could not persist the tag on the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  /**
   * Updates a tag in the database
   *
   * @param entities the tags to be updated
   *
   * @return RuntimeException if an error happened at runtime
   */
  override fun updateEntities(vararg entities: Tag) {
    val databaseSession = this.databaseSessionBuilder.build()
    val transaction = databaseSession.manager.transaction

    try {
      transaction.begin()
      entities.forEach { databaseSession.manager.merge(it) }
      transaction.commit()
    } catch (e: Exception) {
      this.rollbackTransactionIfActive(transaction)
      throw RuntimeException("Could not update the tag on the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  /**
   * Remove a tag from the database
   *
   * @param entities the tags to be removed
   *
   * @throws RuntimeException if an error happens at runtime
   */
  override fun removeEntities(vararg entities: Tag) {
    val databaseSession = this.databaseSessionBuilder.build()
    val transaction = databaseSession.manager.transaction

    try {
      transaction.begin()
      entities.forEach {
        databaseSession.manager.remove(
          databaseSession.manager.find(Tag::class.java, it.id)
        )
      }
      transaction.commit()
    } catch(e: Exception) {
      this.rollbackTransactionIfActive(transaction)
      throw RuntimeException("Could not remove the tag from the database: ${e.message}")
    } finally {
      databaseSession.close()
    }
  }

  private fun rollbackTransactionIfActive(transaction: EntityTransaction) {
    if (!transaction.isActive) return

    transaction.rollback()
  }
}
