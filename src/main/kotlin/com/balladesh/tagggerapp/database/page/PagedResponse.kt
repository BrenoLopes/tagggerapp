package com.balladesh.tagggerapp.database.page

class PagedResponse<T>(
  val page: Int,
  val pageSize: Int,
  private val totalItems: Long,
  val itemsList: List<T>
) {

  val totalPages: Int
    get() {
      val total: Int = if (pageSize == 0) {
        1
      } else {
        ((totalItems - 1) / pageSize).toInt() + 1
      }

      return if (total == 0) { 1 } else { total }
    }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PagedResponse<*>

    if (page != other.page) return false
    if (pageSize != other.pageSize) return false
    if (itemsList != other.itemsList) return false
    if (totalPages != other.totalPages) return false

    return true
  }

  override fun hashCode(): Int {
    var result = page
    result = 31 * result + pageSize
    result = 31 * result + itemsList.hashCode()
    result = 31 * result + totalPages
    return result
  }

  override fun toString(): String {
    return "PagedResponse(page=$page, pageSize=$pageSize, itemsList=$itemsList, totalPages=$totalPages)"
  }
}
