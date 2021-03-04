package com.balladesh.tagggerapp.database.entities

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class FileTest {
  @Test
  fun testObjectIntegrity() {
    val name  = "randomfile01"
    val path  = "randomfilepath01"
    val id    = 1L
    val tags  = arrayListOf<Tag>()
    val date: LocalDateTime  = LocalDateTime.now()

    val file01 = File(id, name, path, date, tags)

    assertEquals(name, file01.name)
    assertEquals(path, file01.path)
    assertEquals(id, file01.id)
    assertEquals(tags, file01.tags)
    assertEquals(date, file01.date)
  }

  @Test
  fun testIfTagsAreCopied() {
    val name  = "randomfile01"
    val path  = "randomfilepath01"
    val id    = 1L
    val tags  = arrayListOf(Tag("Tag01"), Tag("Tag02"))
    val date: LocalDateTime  = LocalDateTime.now()

    val file01 = File(id, name, path, date, tags)

    val copiedTags = file01.tags
    tags.add(Tag("Tag03"))

    assertNotEquals(tags, copiedTags)
  }

  @Test
  fun testIfAddingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    val fileList = listOf(file)
    val tagList = listOf(tag)

    tag.addFiles(file)

    assertEquals(fileList, tag.files)
    assertEquals(tagList, file.tags)
  }

  @Test
  fun testIfRemovingIsPropagated() {
    val tag = Tag("Tag01")
    val file = File("file01", "pathtofile01")

    tag.addFiles(file)
    file.removeTags(tag)

    assertEquals(emptyList(), tag.files)
    assertEquals(emptyList(), file.tags)
  }

  @Test
  fun testJsonConvertion() {
    val date = LocalDateTime.now()

    val tag = Tag(1, "Tag01")
    val file = File(1, "file01", "pathfile01", date, arrayListOf())
    file.addTags(tag)

    val expected = "{\"id\":1,\"name\":\"file01\",\"path\":\"pathfile01\",\"date\":\"$date\",\"tags\":[{\"id\":1,\"name\":\"Tag01\"}]}"
    val received = ObjectMapper().writeValueAsString(file)

    assertEquals(expected, received)
  }
}
