package com.sksamuel.elastic4s.legacy.examples

import com.sksamuel.elastic4s.legacy._

class ClusterSettingsDotDsl extends ElasticDsl {

  clusterPersistentSettings(Map("a" -> "b", "c" -> "d"))

  clusterTransientSettings(Map("f" -> "g"))

  clusterPersistentSettings(Map("a" -> "b", "c" -> "d")).transientSettings(Map("f" -> "g"))

  clusterTransientSettings(Map("f" -> "g")).persistentSettings(Map("f" -> "g"))

}
