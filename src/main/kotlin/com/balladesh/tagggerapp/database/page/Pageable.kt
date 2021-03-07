package com.balladesh.tagggerapp.database.page

enum class Sort {
  ASC,
  DESC;
}

class Pageable() {
  var page: Int
  var maxSize: Int
  var sort: Sort

  init {
    this.page = 0
    this.maxSize = 0
    this.sort = Sort.ASC
  }

  constructor(page: Int, maxSize: Int, sort: Sort) : this() {
    this.page = page
    this.maxSize = maxSize
    this.sort = sort
  }
}
