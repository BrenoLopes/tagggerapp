package com.balladesh.tagggerapp.database.entities

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class TagTest {
  @Test
  fun testObjectIntegrity() {
    val name  = "Tag01"
    val id    = 1L

    val tag = Tag(id, name)

    assertEquals(name, tag.name)
    assertEquals(id, tag.id)
  }

  @Test
  fun testIfFileAreCopied() {
    val name  = "Tag01"
    val id    = 1L

    val tag = Tag(id, name)

    val originalFiles = arrayListOf(
      File("file01", "pathfile01"),
      File("file02", "pathfile02")
    )

    val copiedFiles = tag.files
    originalFiles.add(File("file03", "pathfile03"))

    assertNotEquals(originalFiles, copiedFiles)
  }

  @Test
  fun testIfAddingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    val fileList = listOf(file)
    val tagList = listOf(tag)

    // Should also include it in the tag's object
    file.addTags(tag)

    assertEquals(fileList, tag.files)
    assertEquals(tagList, file.tags)
  }

  @Test
  fun testIfRemovingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    file.addTags(tag)
    tag.removeFiles(file)

    assertEquals(emptyList(), tag.files)
    assertEquals(emptyList(), file.tags)
  }

  @Test
  fun testJsonConvertion() {
    val time = LocalDateTime.now()
    val file = File(1, "file01", "pathfile01", time, mutableListOf())
    val tag = Tag(1, "Tag01")
    tag.addFiles(file)

    val expected = "{\"id\":1,\"name\":\"Tag01\",\"files\":[{\"id\":1,\"name\":\"file01\",\"path\":\"pathfile01\",\"date\":\"$time\"}]}"
    val received = ObjectMapper().writeValueAsString(tag)

    assertEquals(expected, received)
  }
}