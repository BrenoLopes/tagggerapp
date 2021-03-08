package com.balladesh.tagggerapp.database.repositories

import com.balladesh.tagggerapp.database.page.Pageable
import com.balladesh.tagggerapp.database.page.PagedResponse
import java.util.*

interface PageableRepository<T> {
  fun findAll(pageable: Pageable = Pageable()): PagedResponse<T>
  fun findById(id: Long): Optional<T>
  fun findByName(name: String, pageable: Pageable = Pageable()): PagedResponse<T>
  fun persistEntities(vararg entities: T)
  fun updateEntities(vararg entities: T)
  fun removeEntities(vararg entities: T)
}