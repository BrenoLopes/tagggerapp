package com.balladesh.tagggerapp.main

import com.balladesh.tagggerapp.database.entities.Fruit
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javax.persistence.Persistence
import kotlin.system.exitProcess

class HelloWorldApplication: Application()
{
  override fun start(stage: Stage?) {
    try {
      if (stage == null)
        throw NullPointerException("Could not create an application window.")
    } catch (e: NullPointerException) {
      println("Could not create the application's window. Please check if you have permissions to use the current directory.")
      e.printStackTrace()
      exitProcess(1)
    }

    val helloWorld = Label("Hello World")

    val panel = HBox()
    panel.children.add(helloWorld)
    panel.alignment = Pos.CENTER

    val scene = Scene(panel, 450.0, 450.0)

    checkPersistDatabase()
    checkReadDatabase()

    stage.scene = scene
    stage.title = "Hello World"
    stage.isResizable = false
    stage.sizeToScene()
    stage.centerOnScreen()
    stage.show()
  }

  fun checkPersistDatabase() {
    val dbfactory = Persistence.createEntityManagerFactory("app.database")
    val dbmanager = dbfactory.createEntityManager()

    dbmanager.transaction.begin()

    val fruit = Fruit()
    fruit.name = "apple"
    fruit.colour = "red"
    fruit.calories = 5

    dbmanager.persist(fruit)

    dbmanager.transaction.commit()
    dbmanager.close()
    dbfactory.close()
  }

  fun checkReadDatabase() {
    val dbfactory = Persistence.createEntityManagerFactory("app.database")
    val dbmanager = dbfactory.createEntityManager()

    val query = dbmanager.createQuery("from Fruit", Fruit::class.java)
    val fruits = query.resultList

    println("Fruit table has ${fruits.size} records.")

    dbmanager.close()
    dbfactory.close()
  }
}
