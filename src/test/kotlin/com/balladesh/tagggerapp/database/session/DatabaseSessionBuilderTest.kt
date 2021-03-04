package com.balladesh.tagggerapp.database.session

import com.balladesh.tagggerapp.database.TestDatabaseConnection
import kotlin.test.Test
import kotlin.test.assertTrue

internal class DatabaseSessionBuilderTest {
  @Test
  fun testBuild() {
    val testTarget = DatabaseSessionBuilder(TestDatabaseConnection().persistenceUnitName)
    val dbSession = testTarget.build()
    val isSessionOpen = dbSession.manager.isOpen

    dbSession.close()

    assertTrue(isSessionOpen)
  }
}