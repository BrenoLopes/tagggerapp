package com.balladesh.tagggerapp.database.page

class PagedResponse<T>(
  val page: Int,
  val pageSize: Int,
  private val totalItems: Long,
  val itemsList: List<T>
) {

  val totalPages: Int
    get() {
      val total: Int = if (pageSize == 0) { 1 } else { (totalItems / pageSize).toInt() }

      return if (total == 0) { 1 } else { total }
    }
}