package com.sksamuel.elastic4s.legacy.admin

import com.sksamuel.elastic4s.legacy.ElasticDsl
import org.scalatest.FunSuite

class RefreshIndexDslTest extends FunSuite with ElasticDsl {

  test("refresh index dsl compiles") {
    refresh index "myindex"
  }
}
