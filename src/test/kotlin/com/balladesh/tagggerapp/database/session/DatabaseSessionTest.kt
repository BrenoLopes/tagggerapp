package com.balladesh.tagggerapp.database.session

import com.balladesh.tagggerapp.database.DatabaseConnection
import javax.persistence.Persistence
import kotlin.test.Test
import kotlin.test.assertFalse

internal class DatabaseSessionTest {
  @Test
  fun testClose() {
    val sessionFactory = Persistence.createEntityManagerFactory(DatabaseConnection().persistenceUnitName)
    val sessionManager = sessionFactory.createEntityManager()

    val session = DatabaseSession(sessionFactory, sessionManager)
    session.close()

    assertFalse(sessionManager.isOpen)
    assertFalse(sessionFactory.isOpen)
  }
}