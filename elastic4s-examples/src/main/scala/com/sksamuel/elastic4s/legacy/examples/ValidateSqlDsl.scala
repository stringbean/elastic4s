package com.sksamuel.elastic4s.legacy.examples

import com.sksamuel.elastic4s.legacy.ElasticDsl

// examples of the count API in dot notation
class ValidateSqlDsl extends ElasticDsl {

  // simple query being validated
  validate in "index" / "type" query "id:123"

  // validating boolean query
  validate in "index" / "type" query {
    bool {
      must {
        termQuery("name", "sammy")
      } should {
        termQuery("place", "buckinghamshire")
      }
    }
  }
}


