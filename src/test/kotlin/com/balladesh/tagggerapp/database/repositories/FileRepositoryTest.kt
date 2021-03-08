package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.TestDatabaseConnection
import com.balladesh.tagggerapp.database.entities.File
import com.balladesh.tagggerapp.database.entities.Tag
import com.balladesh.tagggerapp.database.page.Pageable
import com.balladesh.tagggerapp.database.page.PagedResponse
import com.balladesh.tagggerapp.database.page.Sort
import com.balladesh.tagggerapp.database.session.DatabaseSessionBuilder
import org.junit.ComparisonFailure
import java.time.LocalDateTime
import javax.persistence.NoResultException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FileRepositoryTest {
  private val testTarget: FileRepository
  private val dbBuilder: DatabaseSessionBuilder

  init {
    val sessionBuilder = DatabaseSessionBuilder(TestDatabaseConnection().persistenceUnitName)

    this.dbBuilder = DatabaseSessionBuilder(TestDatabaseConnection().persistenceUnitName)
    this.testTarget = FileRepository(sessionBuilder)
  }

  @AfterTest
  fun destroyData() {
    this.cleanDatabase()
  }

  @Test
  fun testFindAllWithoutPaging() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file02", "pathfile02", LocalDateTime.now(), arrayListOf())
    val file03 = File("file03", "pathfile03", LocalDateTime.now(), arrayListOf())
    val file04 = File("file04", "pathfile04", LocalDateTime.now(), arrayListOf())
    val file05 = File("file05", "pathfile05", LocalDateTime.now(), arrayListOf())
    val file06 = File("file06", "pathfile06", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02, file03, file04, file05, file06)

    val expected = PagedResponse(0, 0, 6, listOf(file01, file02, file03, file04, file05, file06))
    val received = this.testTarget.findAll()

    assertEquals(expected, received)
  }

  @Test
  fun testFindAllByAccessingTheFirstPage() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file02", "pathfile02", LocalDateTime.now(), arrayListOf())
    val file03 = File("file03", "pathfile03", LocalDateTime.now(), arrayListOf())
    val file04 = File("file04", "pathfile04", LocalDateTime.now(), arrayListOf())
    val file05 = File("file05", "pathfile05", LocalDateTime.now(), arrayListOf())
    val file06 = File("file06", "pathfile06", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02, file03, file04, file05, file06)

    val expectedResponse = PagedResponse(
      0,
      5,
      6,
      listOf(file01, file02, file03, file04, file05)
    )
    val receivedResponse = this.testTarget.findAll(Pageable(0, 5, Sort.ASC))

    val expectedTotalPages = 2
    val receivedTotalPages = receivedResponse.totalPages

    assertEquals(expectedResponse, receivedResponse)
    assertEquals(expectedTotalPages, receivedTotalPages)
  }

  @Test
  fun testFindAllByAccessingTheSecondPage() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file02", "pathfile02", LocalDateTime.now(), arrayListOf())
    val file03 = File("file03", "pathfile03", LocalDateTime.now(), arrayListOf())
    val file04 = File("file04", "pathfile04", LocalDateTime.now(), arrayListOf())
    val file05 = File("file05", "pathfile05", LocalDateTime.now(), arrayListOf())
    val file06 = File("file06", "pathfile06", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02, file03, file04, file05, file06)

    val expectedResponse = PagedResponse(
      1,
      5,
      6,
      listOf(file06)
    )
    val receivedResponse = this.testTarget.findAll(Pageable(1, 5, Sort.ASC))

    val expectedTotalPages = 2
    val receivedTotalPages = receivedResponse.totalPages

    assertEquals(expectedResponse, receivedResponse)
    assertEquals(expectedTotalPages, receivedTotalPages)
  }

  @Test
  fun testFindAllWithPagingInDescending() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file02", "pathfile02", LocalDateTime.now(), arrayListOf())
    val file03 = File("file03", "pathfile03", LocalDateTime.now(), arrayListOf())
    val file04 = File("file04", "pathfile04", LocalDateTime.now(), arrayListOf())
    val file05 = File("file05", "pathfile05", LocalDateTime.now(), arrayListOf())
    val file06 = File("file06", "pathfile06", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02, file03, file04, file05, file06)

    val expectedResponse = PagedResponse(
      0,
      5,
      6,
      listOf(file06, file05, file04, file03, file02)
    )
    val receivedResponse = this.testTarget.findAll(Pageable(0, 5, Sort.DESC))

    val expectedTotalPages = 2
    val receivedTotalPages = receivedResponse.totalPages

    assertEquals(expectedResponse, receivedResponse)
    assertEquals(expectedTotalPages, receivedTotalPages)
  }

  @Test
  fun testSearchById() {
    val expectedFile = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    this.testTarget.persistEntities(expectedFile)

    val receivedFile = this.testTarget.findById(expectedFile.id)
      .orElseThrow { NoResultException::class.java.newInstance() }

    assertEquals(expectedFile, receivedFile)
  }

  @Test
  fun testFindByName() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file01", "pathfile02", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02)

    val expected = PagedResponse(
      0,
      0,
      2,
      listOf(file01, file02)
    )
    val received = this.testTarget.findByName("file01")

    assertEquals(expected, received)
  }

  @Test
  fun testFindByNameWithPaging() {
    val file01 = File("file01", "pathfile01", LocalDateTime.now(), arrayListOf())
    val file02 = File("file01", "pathfile02", LocalDateTime.now(), arrayListOf())

    this.testTarget.persistEntities(file01, file02)

    val expected = PagedResponse(
      0,
      1,
      2,
      listOf(file01)
    )
    val received = this.testTarget.findByName("file01", Pageable(0, 1, Sort.ASC))

    assertEquals(expected, received)
  }

  @Test
  fun testFindByTagWithoutPaging() {
    val tag = Tag("JustATag")

    val file01 = File("file01", "path01")
    val file02 = File("file02", "path02")

    tag.addFiles(file01, file02)
    testTarget.persistEntities(file01, file02)

    val expected = PagedResponse(0, 0, 2, listOf(file01, file02))

    val received = testTarget.findByTag(tag)

    assertEquals(expected, received)
  }

  @Test
  fun testUpdate() {
    val file01 = File("file01", "path01")
    testTarget.persistEntities(file01)

    file01.name = "editedfile01"
    testTarget.updateEntities(file01)

    val received = testTarget.findById(file01.id).orElseThrow { ComparisonFailure::class.java.newInstance() }

    assertEquals(file01, received)
  }

  @Test
  fun testDelete() {
    val file01 = File("file01", "path01")
    val file02 = File("file02", "path02")

    testTarget.persistEntities(file01, file02)
    testTarget.removeEntities(file01, file02)

    val expected = PagedResponse<File>(0, 0, 0, emptyList())
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