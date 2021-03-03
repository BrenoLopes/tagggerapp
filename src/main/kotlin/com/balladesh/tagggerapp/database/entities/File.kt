package com.balladesh.tagggerapp.database.entities

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class File(): Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id = Int.MIN_VALUE

  var name = ""
  var path = ""

  var date = LocalDateTime.now()

  @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
  @JoinTable(
    name = "file_tags",
    joinColumns = [JoinColumn(name = "file_id", referencedColumnName = "id")],
    inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
  )
  var tags: MutableList<Tag> = mutableListOf()
    private set

  constructor(name: String, path: String): this() {
    this.name = name
    this.path = path
  }

  constructor(name: String, path: String, date: LocalDateTime?, tags: MutableList<Tag>): this() {
    this.name = name
    this.path = path
    this.date = date
    this.tags = tags
  }

  fun addTag(tag: Tag, shouldCascade: Boolean = true) {
    if (!this.tags.contains(tag)) {
      this.tags.add(tag)
    } else {
      this.tags[this.tags.indexOf(tag)] = tag
    }

    if (shouldCascade)
      tag.addFile(this, false)
  }

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
    var result = id
    result = 31 * result + name.hashCode()
    result = 31 * result + path.hashCode()
    result = 31 * result + (date?.hashCode() ?: 0)
    result = 31 * result + tags.hashCode()
    return result
  }

  override fun toString(): String {
    val tags = this.tags.map { tag -> tag.name }
    return "File(id=$id, name='$name', path='$path', date=$date, tags=$tags)"
  }
}
