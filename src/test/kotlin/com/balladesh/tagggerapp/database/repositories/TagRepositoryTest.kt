package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.TestDatabaseConnection
import com.balladesh.tagggerapp.database.entities.File
import com.balladesh.tagggerapp.database.entities.Tag
import com.balladesh.tagggerapp.database.page.Pageable
import com.balladesh.tagggerapp.database.page.PagedResponse
import com.balladesh.tagggerapp.database.page.Sort
import com.balladesh.tagggerapp.database.session.DatabaseSessionBuilder
import javax.persistence.EntityNotFoundException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect

internal class TagRepositoryTest {
  private val testTarget: TagRepository
  private val dbBuilder: DatabaseSessionBuilder

  init {
    val testDbUnitName = TestDatabaseConnection().persistenceUnitName
    val sessionBuilder = DatabaseSessionBuilder(testDbUnitName)

    this.dbBuilder = DatabaseSessionBuilder(testDbUnitName)
    this.testTarget = TagRepository(sessionBuilder)
  }

  @AfterTest
  fun destroyData() {
    this.cleanDatabase()
  }

  @Test
  fun findAllWithoutPaging() {
    val tag01 = Tag("thetag01")
    val tag02 = Tag("thetag02")
    val tag03 = Tag("thetag03")
    val tag04 = Tag("thetag04")
    val tag05 = Tag("thetag05")

    testTarget.persistEntities(tag01, tag02, tag03, tag04, tag05)

    val expected = PagedResponse(0, 0, 5, listOf(tag01, tag02, tag03, tag04, tag05))
    val received = this.testTarget.findAll()

    assertEquals(expected, received)
  }

  @Test
  fun findAllWithPaging() {
    val tag01 = Tag("thetag01")
    val tag02 = Tag("thetag02")
    val tag03 = Tag("thetag03")
    val tag04 = Tag("thetag04")
    val tag05 = Tag("thetag05")

    testTarget.persistEntities(tag01, tag02, tag03, tag04, tag05)

    val expected = PagedResponse(1, 3, 5, listOf(tag04, tag05))
    val received = this.testTarget.findAll(Pageable(1, 3, Sort.ASC))

    assertEquals(expected, received)
  }

  @Test
  fun findAllWithPagingInDescending() {
    val tag01 = Tag("thetag01")
    val tag02 = Tag("thetag02")
    val tag03 = Tag("thetag03")
    val tag04 = Tag("thetag04")
    val tag05 = Tag("thetag05")

    testTarget.persistEntities(tag01, tag02, tag03, tag04, tag05)

    val expected = PagedResponse(1, 3, 5, listOf(tag02, tag01))
    val received = this.testTarget.findAll(Pageable(1, 3, Sort.DESC))

    assertEquals(expected, received)
  }

  @Test
  fun findById() {
    val expected = Tag("randomtag")

    testTarget.persistEntities(expected)

    val received = testTarget.findById(expected.id)
      .orElseThrow { EntityNotFoundException::class.java.newInstance() }

    assertEquals(expected, received)
  }

  @Test
  fun findByName() {
    val tag01 = Tag("randomtag")
    val tag02 = Tag("randomtag")
    testTarget.persistEntities(tag01, tag02)

    val expected = PagedResponse(0, 0, 1, listOf(tag01, tag02))
    val received = testTarget.findByName(tag01.name)

    assertEquals(expected, received)
  }

  @Test
  fun findByFile() {
    val file = File("randomfile.txt", "files/01/randompath")
    val tag = Tag("randomtag")
    tag.addFiles(file)

    testTarget.persistEntities(tag)

    val expected = PagedResponse(0, 0, 1, listOf(tag))
    val received = testTarget.findByFile(file)

    assertEquals(expected, received)
  }

  @Test
  fun updateEntities() {
    val tag = Tag("randomtag")
    testTarget.persistEntities(tag)

    tag.name = "anotherrandomtag"
    testTarget.updateEntities(tag)

    val received = testTarget.findById(tag.id)
      .orElseThrow { EntityNotFoundException::class.java.newInstance() }

    assertEquals(tag, received)
  }

  @Test
  fun removeEntities() {
    val tag = Tag("randomtag")
    testTarget.persistEntities(tag)

    testTarget.removeEntities(tag)
    val expected = PagedResponse(0, 0, 0, emptyList<Tag>())
    val received = testTarget.findAll()

    assertEquals(expected, received)
  }

  private fun cleanDatabase() {
    val session = this.dbBuilder.build()

    val transaction = session.manager.transaction

    transaction.begin()
    val delTagQuery = session.manager.createQuery("DELETE FROM Tag")
    val delFileQuery = session.manager.createQuery("DELETE FROM File")

    delTagQuery.executeUpdate()
    delFileQuery.executeUpdate()
    session.flush()
    transaction.commit()
  }
}