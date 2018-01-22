package com.sksamuel.elastic4s.legacy.admin

import com.sksamuel.elastic4s.legacy.ElasticDsl
import org.scalatest.FunSuite

class SegmentsDslTest extends FunSuite with ElasticDsl {

  test("segments dsl compiles") {
    get segments "myindex"
  }
}
