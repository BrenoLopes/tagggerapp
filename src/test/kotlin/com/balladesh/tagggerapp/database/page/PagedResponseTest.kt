package com.balladesh.tagggerapp.database.page

import kotlin.test.Test
import kotlin.test.assertEquals

internal class PagedResponseTest {
  @Test
  fun testTotalPage_ShouldReturn1() {
    val expected = 1
    val testTarget = PagedResponse(0, 5, 5, listOf(1, 2, 3, 4, 5))

    assertEquals(expected, testTarget.totalPages)
  }

  @Test
  fun testTotalPage_ShouldReturn1_WithNoPage() {
    val expected = 1
    val testTarget = PagedResponse(0, 0, 5, listOf(1, 2, 3, 4, 5))

    assertEquals(expected, testTarget.totalPages)
  }

  @Test
  fun testTotalPage_ShouldReturn2() {
    val expected = 2
    val testTarget = PagedResponse(0, 5, 10, listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    assertEquals(expected, testTarget.totalPages)
  }
}