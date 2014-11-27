package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest

trait ClusterDsl {
  def clusterHealth = new ClusterHealthDefinition()
  def clusterHealth(indices: String*) = new ClusterHealthDefinition(indices: _*)
}

class ClusterHealthDefinition(indices: String*) {
  val _builder = new ClusterHealthRequest(indices: _*)

  def build = _builder

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }
}
