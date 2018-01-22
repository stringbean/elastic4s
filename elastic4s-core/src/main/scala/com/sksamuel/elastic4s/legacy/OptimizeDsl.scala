package com.sksamuel.elastic4s.legacy

import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse
import org.elasticsearch.client.{Client, Requests}

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait OptimizeDsl {

  implicit object OptimizeDefinitionExecutable
    extends Executable[OptimizeDefinition, OptimizeResponse, OptimizeResponse] {
    override def apply(c: Client, t: OptimizeDefinition): Future[OptimizeResponse] = {
      injectFuture(c.admin.indices.optimize(t.build, _))
    }
  }
}

class OptimizeDefinition(indexes: String*) {

  private val builder = Requests.optimizeRequest(indexes: _*)
  def build = builder

  def flush(flush: Boolean): OptimizeDefinition = {
    builder.flush(flush)
    this
  }

  def maxSegments(maxSegments: Int): OptimizeDefinition = {
    builder.maxNumSegments(maxSegments)
    this
  }

  /**
   * Should the optimization only expunge deletes from the index, without full optimization.
   * Defaults to full optimization (<tt>false</tt>).
   */

  def onlyExpungeDeletes(onlyExpungeDeletes: Boolean): OptimizeDefinition = {
    builder.onlyExpungeDeletes(onlyExpungeDeletes)
    this
  }

  @deprecated("deprecated in elasticsearch", "1.6.1")
  def force(force: Boolean): OptimizeDefinition = {
    builder.force(force)
    this
  }
}
