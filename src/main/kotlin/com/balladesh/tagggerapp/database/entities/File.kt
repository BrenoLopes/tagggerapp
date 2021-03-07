package com.balladesh.tagggerapp.database.entities

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList
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
   * Returns the tags associated with this file in an immutable list
   */
  @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
  @JoinTable(
    name = "file_tags",
    joinColumns = [JoinColumn(name = "file_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
  )
  var tags: List<Tag> = emptyList()
    get() = ArrayList(field)
    private set

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
   * Adds a tag into this file. This method will also include this object into this tag, unless shouldCascade is set
   * to false.
   *
   * @param tags The tags to be added
   * @param shouldCascade Controls if this method should also include this file into the tag's object
   */
  fun addTags(vararg tags: Tag, shouldCascade: Boolean = true) {
    val newTags = ArrayList(this.tags)

    tags.forEach {
      if (!newTags.contains(it)) {
        newTags.add(it)
      } else {
        newTags[newTags.indexOf(it)] = it
      }

      if (shouldCascade)
        it.addFiles(files = arrayOf(this), shouldCascade = false)
    }

    this.tags = newTags
  }

  /**
   * Remove a tag from this file. This method will also remove this object from this tag, unless shouldCascade is set to
   * false.
   *
   * @param tags The tags to be removed from this file
   * @param shouldCascade Controls if this method should also remove this file from the tag's object
   */
  fun removeTags(vararg tags: Tag, shouldCascade: Boolean = true) {
    val newTags = ArrayList(this.tags)

    tags.forEach {
      newTags.remove(it)

      if (shouldCascade)
        it.removeFiles(files = arrayOf(this), shouldCascade = false)
    }

    this.tags = newTags
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as File

    if (id != other.id) return false
    if (name != other.name) return false
    if (path != other.path) return false
    if (date != other.date) return false
    if (tags.size != other.tags.size) return false

    for (tags in tags) {
      for (otherTags in other.tags) {
        if (tags.id != otherTags.id) return false
        if (tags.name != otherTags.name) return false
      }
    }

    return true
  }

  override fun hashCode(): Int {
    var result = Objects.hash(id, name, path, date)
    tags.forEach{ result += Objects.hash(it.id, it.name) }

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
