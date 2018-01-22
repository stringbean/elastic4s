package com.sksamuel.elastic4s.legacy.examples

import com.sksamuel.elastic4s.legacy._

class ClusterSettingsSqlDsl extends ElasticDsl {

  cluster persistentSettings Map("a" -> "b", "c" -> "d")

  cluster persistentSettings Map("a" -> "b", "c" -> "d") transientSettings Map("e" -> "f")

  cluster transientSettings Map("a" -> "b", "c" -> "d")

  cluster transientSettings Map("a" -> "b", "c" -> "d") persistentSettings Map("e" -> "f")
}
