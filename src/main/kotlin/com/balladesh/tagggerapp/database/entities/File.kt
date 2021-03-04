package com.balladesh.tagggerapp.database.entities

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Throws

/**
 * An entity to represent and map the file's table in the database.
 */
@Entity
class File(): Serializable {
  companion object {
    private const val serialVersionUID: Long = 2140480238434902387L
  }

  /**
   * This file's id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id = Long.MIN_VALUE

  /**
   * The original name of this file
   */
  var name = ""

  /**
   * This file's path inside the local cache
   */
  var path = ""

  /**
   * The date this file was added in this app
   */
  var date: LocalDateTime = LocalDateTime.now()

  /**
   * The tags associated with this file
   */
  @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
  @JoinTable(
    name = "file_tags",
    joinColumns = [JoinColumn(name = "file_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
  )
  private var tags: MutableList<Tag> = arrayListOf()

  /**
   * Simple constructor for adding a file into the database
   */
  constructor(name: String, path: String): this() {
    this.name = name
    this.path = path
  }

  /**
   * Complete constructor for creating a complete instance. This can maybe be used by an ORM
   */
  constructor(name: String, path: String, date: LocalDateTime, tags: MutableList<Tag>): this() {
    this.name = name
    this.path = path
    this.date = date
    this.tags = tags
  }

  constructor(id: Long, name: String, path: String, date: LocalDateTime, tags: MutableList<Tag>): this() {
    this.id = id
    this.name = name
    this.path = path
    this.date = date
    this.tags = tags
  }

  /**
   * Returns an immutable copy of all the tags associated with this file
   */
  fun getTags(): List<Tag> {
    return this.tags.toList()
  }

  /**
   * Adds a tag into this file. This method will also include this object into this tag, unless shouldCascade is set
   * to false.
   *
   * @param tag The tag to add
   * @param shouldCascade Controls if this method should also include this file into the tag's object
   */
  fun addTag(tag: Tag, shouldCascade: Boolean = true) {
    if (!this.tags.contains(tag)) {
      this.tags.add(tag)
    } else {
      this.tags[this.tags.indexOf(tag)] = tag
    }

    if (shouldCascade)
      tag.addFile(this, false)
  }

  /**
   * Remove a tag from this file. This method will also remove this object from this tag, unless shouldCascade is set to
   * false.
   *
   * @param tag The tag to remove from this file
   * @param shouldCascade Controls if this method should also remove this file from the tag's object
   */
  fun removeTag(tag: Tag, shouldCascade: Boolean = true) {
    this.tags.remove(tag)

    if (shouldCascade)
      tag.removeFile(this, false)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as File

    if (id != other.id) return false
    if (name != other.name) return false
    if (path != other.path) return false
    if (date != other.date) return false
    if (tags != other.tags) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + path.hashCode()
    result = 31 * result + date.hashCode()
    result = 31 * result + tags.hashCode()
    return result
  }

  /**
   * A method to convert this object into json.
   *
   * @throws RuntimeException
   */
  @JsonValue
  @Throws(RuntimeException::class)
  fun toJson(): JsonNode {
    try {
      val map = mutableMapOf<String, Any>()
      map["id"] = this.id
      map["name"] = this.name
      map["path"] = this.path
      map["date"] = this.date.toString()
      map["tags"] = this.tags.map {
        mutableMapOf<String, Any>(Pair("id", it.id), Pair("name", it.name))
      }

      return ObjectMapper()
        .valueToTree(map)
    } catch (e: Exception) {
      throw RuntimeException("An error happened when trying to serialize a File into Json")
    }
  }

  /**
   * A method to convert this object into a normal string format
   */
  override fun toString(): String {
    val tags = this.tags.map { tag -> tag.name }
    return "File(id=$id, name='$name', path='$path', date=$date, tags=$tags)"
  }
}
