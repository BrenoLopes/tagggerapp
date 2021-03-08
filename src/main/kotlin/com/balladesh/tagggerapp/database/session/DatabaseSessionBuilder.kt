package com.balladesh.tagggerapp.database.session

import com.balladesh.tagggerapp.database.DatabaseConnection
import javax.persistence.Persistence

/**
 * Build a database entity object
 *
 * @throws IllegalStateException if the entity manager factory has been closed
 */
class DatabaseSessionBuilder(private val persistenceUnitName: String = DatabaseConnection().persistenceUnitName) {
  fun build(): DatabaseSession {
    val factory = Persistence.createEntityManagerFactory(persistenceUnitName)
    val manager = factory.createEntityManager()

    return DatabaseSession(factory, manager)
  }
}