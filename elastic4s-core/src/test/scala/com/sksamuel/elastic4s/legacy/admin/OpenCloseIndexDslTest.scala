package com.sksamuel.elastic4s.legacy.admin

import com.sksamuel.elastic4s.legacy.ElasticDsl
import org.scalatest.FunSuite

class OpenCloseIndexDslTest extends FunSuite with ElasticDsl {

  test("open index dsl compiles") {
    open index "myindex"
  }

  test("close index dsl compiles") {
    close index "myindex"
  }
}
