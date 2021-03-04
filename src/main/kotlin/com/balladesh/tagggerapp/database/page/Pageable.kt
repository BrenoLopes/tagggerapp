package com.balladesh.tagggerapp.database.page

data class Pageable(val page: Int, val maxSize: Int, val sort: Sort)