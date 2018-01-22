package com.sksamuel.elastic4s.legacy

import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.bulk.{BulkItemResponse, BulkItemRequest, BulkRequest, BulkResponse}
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait BulkCompatibleDefinition

trait BulkDsl {
  this: IndexDsl =>

  def bulk(requests: Iterable[BulkCompatibleDefinition]): BulkDefinition = new BulkDefinition(requests.toSeq)
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = bulk(requests)

  implicit object BulkCompatibleDefinitionExecutable
    extends Executable[Seq[BulkCompatibleDefinition], BulkResponse, BulkResponse] {
    override def apply(c: Client, ts: Seq[BulkCompatibleDefinition]): Future[BulkResponse] = {
      val bulk = c.prepareBulk()
      ts.foreach {
        case index: IndexDefinition => bulk.add(index.build)
        case delete: DeleteByIdDefinition => bulk.add(delete.build)
        case update: UpdateDefinition => bulk.add(update.build)
      }
      injectFuture(bulk.execute)
    }
  }

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, BulkResponse] {
    override def apply(c: Client, t: BulkDefinition): Future[BulkResponse] = {
      injectFuture(c.bulk(t.build, _))
    }
  }

  implicit def javatoScala(resp: BulkResponse): BulkResult = new BulkResult(resp)
}

class BulkDefinition(val requests: Seq[BulkCompatibleDefinition]) {

  def build = _builder

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(value: TimeValue): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(duration: Duration): this.type = {
    _builder.timeout(TimeValue.timeValueMillis(duration.toMillis))
    this
  }

  def replicationType(replicationType: ReplicationType): this.type = {
    _builder.replicationType(replicationType)
    this
  }

  def refresh(refresh: Boolean): this.type = {
    _builder.refresh(refresh)
    this
  }

  def consistencyLevel(level: WriteConsistencyLevel): this.type = {
    _builder.consistencyLevel(level)
    this
  }

  private val _builder = new BulkRequest()
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
    case register: RegisterDefinition => _builder.add(register.build)
  }
}

case class BulkResult(response: BulkResponse) {

  import scala.concurrent.duration._

  def items: Array[BulkItemResponse] = response.getItems
  def took: Duration = response.getTook.millis.millis
}
