package com.balladesh.tagggerapp.main

import com.balladesh.tagggerapp.database.entities.File
import com.balladesh.tagggerapp.database.entities.Tag
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
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

//    checkPersistDatabase()
//    checkReadDatabase()

    stage.scene = scene
    stage.title = "Hello World"
    stage.isResizable = false
    stage.sizeToScene()
    stage.centerOnScreen()
    stage.show()
  }

//  fun checkPersistDatabase() {
//    val dbfactory = Persistence.createEntityManagerFactory("app.database")
//    val dbmanager = dbfactory.createEntityManager()
//
//    dbmanager.transaction.begin()
//
//    val file = File("ohno.txt", "02/ohno-1231321321.txt")
//
//    val tag = Tag("amongus")
//    file.addTag(tag)
//
//    dbmanager.persist(file)
//
//    dbmanager.transaction.commit()
//    dbmanager.close()
//    dbfactory.close()
//  }
//
//  fun checkReadDatabase() {
//    val dbfactory = Persistence.createEntityManagerFactory("app.database")
//    val dbmanager = dbfactory.createEntityManager()
//
//    val query = dbmanager.createQuery("FROM File f WHERE f.name = :name")
//      .setParameter("name", "ohno.txt")
//    val fileList: List<File> = query.resultList as List<File>
//
//    println("Persisted File: ")
//    println(mapper.writeValueAsString(fileList))
//
//    dbmanager.close()
//    dbfactory.close()
//  }
}
