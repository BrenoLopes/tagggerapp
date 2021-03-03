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

    val files = arrayListOf(
      File("file01", "pathfile01"),
      File("file02", "pathfile02")
    )

    val copiedFiles = tag.getFiles()
    files.add(File("file03", "pathfile03"))

    assertNotEquals(files, copiedFiles)
  }

  @Test
  fun testIfAddingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    val fileList = listOf(file)
    val tagList = listOf(tag)

    // Should also include it in the tag's object
    file.addTag(tag)

    assertEquals(fileList, tag.getFiles())
    assertEquals(tagList, file.getTags())
  }

  @Test
  fun testIfRemovingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    file.addTag(tag)
    tag.removeFile(file)

    assertEquals(emptyList(), tag.getFiles())
    assertEquals(emptyList(), file.getTags())
  }

  @Test
  fun testJsonConvertion() {
    val time = LocalDateTime.now()
    val file = File(1, "file01", "pathfile01", time, mutableListOf())
    val tag = Tag(1, "Tag01")
    tag.addFile(file)

    val expected = "{\"id\":1,\"name\":\"Tag01\",\"files\":[{\"id\":1,\"name\":\"file01\",\"path\":\"pathfile01\",\"date\":\"$time\"}]}"
    val received = ObjectMapper().writeValueAsString(tag)

    assertEquals(expected, received)
  }
}