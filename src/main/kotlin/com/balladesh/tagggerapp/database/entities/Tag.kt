package com.balladesh.tagggerapp.database.entities

import java.io.Serializable
import javax.persistence.*

@Entity
class Tag(): Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id = Int.MIN_VALUE

  var name = ""

  @ManyToMany(mappedBy = "tags", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
  var files: MutableList<File> = mutableListOf()
    private set

  constructor(name: String): this() {
    this.name = name
  }

  fun addFile(file: File, shouldCascade: Boolean = true) {
    if (!this.files.contains(file)) {
      this.files.add(file)
    } else {
      this.files[this.files.indexOf(file)] = file
    }

    if (shouldCascade)
      file.addTag(this, false)
  }

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
    var result = id
    result = 31 * result + name.hashCode()
    result = 31 * result + files.hashCode()
    return result
  }

  override fun toString(): String {
    val files = this.files.map { file -> file.name }
    return "Tag(id=$id, name='$name', files=$files)"
  }
}