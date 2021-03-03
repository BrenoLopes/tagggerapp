package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.entities.Fruit
import com.google.common.collect.ImmutableList
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

class FruitRepository {
  val factory: EntityManagerFactory = Persistence
    .createEntityManagerFactory("app.database");

  fun selectAllFruits(): ImmutableList<Fruit> {
    val dbmanager = this.factory.createEntityManager()

    val query = dbmanager.createQuery("from Fruit", Fruit::class.java)
    val fruits = query.resultList

    return ImmutableList.copyOf(fruits.toList())
  }
}