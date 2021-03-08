package com.balladesh.tagggerapp.database.page

import com.balladesh.tagggerapp.database.session.DatabaseSession
import javax.persistence.Query

/**
 * A class to help the user navigate between the entire query, when the database have too many items
 * and the user needs to break it in multiple small places instead of getting the entire data in one go.
 *
 * @param page current page that this data is fragmented into
 * @param pageSize maximum amount of data in one page. Use 0 or -1 to get the entire data in one query
 * @param totalItems the total count of data that the database has
 * @param dataList the data inside a list
 */
class PagedResponse<T>(
  val page: Int,
  val pageSize: Int,
  private val totalItems: Long,
  val dataList: List<T>
) {
  /**
   * Calculates the total number of pages that the data is fragmented into
   */
  val totalPages: Int
    get() {
      val total: Int = if (pageSize < 1) {
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
    if (dataList != other.dataList) return false
    if (totalPages != other.totalPages) return false

    return true
  }

  override fun hashCode(): Int {
    var result = page
    result = 31 * result + pageSize
    result = 31 * result + dataList.hashCode()
    result = 31 * result + totalPages
    return result
  }

  override fun toString(): String {
    return "PagedResponse(page=$page, pageSize=$pageSize, itemsList=$dataList, totalPages=$totalPages)"
  }
}

/**
 * A factory to quickly query and create a response if the user needs it to be fragmented
 */
interface PagedResponseFactory<T> {
  fun create(dbSession: DatabaseSession, fetchQuery: Query, countQuery: Query, pageable: Pageable): PagedResponse<T>
}

/**
 * A default implementation for the factory. It limits the query if the maximum page size is more then 0
 */
class DefaultResponseFactory<T>: PagedResponseFactory<T> {
  override fun create(dbSession: DatabaseSession, fetchQuery: Query, countQuery: Query, pageable: Pageable): PagedResponse<T> {
    if (pageable.maxSize > 0) {
      fetchQuery.maxResults = pageable.maxSize
      fetchQuery.firstResult = if (pageable.page > 0) { pageable.page * pageable.maxSize } else { 0 }
    }

    @Suppress("UNCHECKED_CAST")
    val fileList = fetchQuery.resultList as List<T>
    val totalOfItems = countQuery.singleResult as Long

    return PagedResponse(pageable.page, pageable.maxSize, totalOfItems, fileList)
  }
}
