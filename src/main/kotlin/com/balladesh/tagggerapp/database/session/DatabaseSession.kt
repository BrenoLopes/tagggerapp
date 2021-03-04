package com.balladesh.tagggerapp.database.session

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class DatabaseSession(private val factory: EntityManagerFactory, val manager: EntityManager) {
  fun flush() {
    if (manager.isOpen) manager.flush()
  }

  fun close() {
    if(manager.isOpen) manager.close()
    if(factory.isOpen) factory.close()
  }
}
