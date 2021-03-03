package com.balladesh.tagggerapp.database.entities

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Fruit(): Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private var id = Int.MIN_VALUE

  var name = ""
  var colour = ""
  var calories = Int.MIN_VALUE

  constructor(id: Int, name: String, colour: String, calories: Int) : this() {
    this.id = id
    this.name = name
    this.colour = colour
    this.calories = calories
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Fruit

    if (id != other.id) return false
    if (name != other.name) return false
    if (colour != other.colour) return false
    if (calories != other.calories) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + name.hashCode()
    result = 31 * result + colour.hashCode()
    result = 31 * result + calories
    return result
  }

  override fun toString(): String {
    return "Fruit(id=$id, name='$name', colour='$colour', calories=$calories)"
  }
}
