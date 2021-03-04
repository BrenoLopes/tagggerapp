package com.balladesh.tagggerapp.database.entities

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Serializable
import javax.persistence.*
import kotlin.jvm.Throws

/**
 * An entity to represent and map the tag's table in the database.
 */
@Entity
class Tag(): Serializable {
  companion object {
    private const val serialVersionUID: Long = 12313123123125346L
  }

  /**
   * This tag's id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id = Long.MIN_VALUE

  /**
   * This tag's name
   */
  var name = ""

  /**
   * All files associated with this tag
   */
  private var files: MutableList<File> = mutableListOf()
  @ManyToMany(mappedBy = "tags", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)

  /**
   * Simple constructor to create a tag with a name
   */
  constructor(name: String): this() {
    this.name = name
  }

  constructor(id: Long, name: String): this() {
    this.id = id
    this.name = name
  }

  /**
   * Returns an immutable copy of all the files associated with this tag
   */
  fun getFiles(): List<File> {
    return this.files.toList()
  }

  /**
   * Associates a file with this tag. This method will also include this object into the file object, unless
   * shouldCascade is set to false.
   *
   * @param file The file to associate
   * @param shouldCascade If this method should also include this tag into the files' object
   */
  fun addFile(file: File, shouldCascade: Boolean = true) {
    if (!this.files.contains(file)) {
      this.files.add(file)
    } else {
      this.files[this.files.indexOf(file)] = file
    }

    if (shouldCascade)
      file.addTag(this, false)
  }

  /**
   * Remove a tag from this file. This method will also remove this object from this tag, unless shouldCascade is set to
   * false.
   *
   * @param file The file to remove from this tag
   * @param shouldCascade Controls if this method should also remove this tag from the files' object
   */
  fun removeFile(file: File, shouldCascade: Boolean = true) {
    this.files.remove(file)

    if (shouldCascade)
      file.removeTag(this, false)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Tag

    if (id != other.id) return false
    if (name != other.name) return false
    if (files != other.files) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + files.hashCode()
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
      map["files"] = this.files.map {
        val f = mutableMapOf<String,Any>()
        f["id"] = it.id
        f["name"] = it.name
        f["path"] = it.path
        f["date"] = it.date.toString()
        f
      }

      return ObjectMapper()
        .valueToTree(map)
    } catch (e: Exception) {
      throw RuntimeException("An error happened when trying to serialize a Tag into Json")
    }
  }

  /**
   * A method to convert this object into a normal string format
   */
  override fun toString(): String {
    val files = this.files.map { file -> file.name }
    return "Tag(id=$id, name='$name', files=$files)"
  }
}