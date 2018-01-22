package com.sksamuel.elastic4s.legacy

import org.elasticsearch.action.count.{CountRequestBuilder, CountResponse}
import org.elasticsearch.action.support.{QuerySourceBuilder, IndicesOptions}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentHelper
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

import scala.concurrent.Future

/** @author Stephen Samuel */
trait CountDsl {

  implicit object CountDefinitionExecutable extends Executable[CountDefinition, CountResponse, CountResponse] {
    override def apply(client: Client, t: CountDefinition): Future[CountResponse] = injectFuture(client
      .count(t.build, _))
  }

  implicit object CountDefinitionShow extends Show[CountDefinition] {
    override def show(f: CountDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class CountDefinitionShowOps(f: CountDefinition) {
    def show: String = CountDefinitionShow.show(f)
  }
}

class CountDefinition(indexesTypes: IndexesTypes) {

  val _builder = new CountRequestBuilder(ProxyClients.client)
    .setIndices(indexesTypes.indexes: _*)
    .setTypes(indexesTypes.types: _*)
    .setQuery(QueryBuilders.matchAllQuery())

  def build = _builder.request()

  /**
   * Expects a query in json format and sets the query of the search request.
   * Query must be valid json beginning with '{' and ending with '}'.
   * Field names must be double quoted.
   *
   * Example:
   * {{{
   * search in "*" types("users", "tweets") limit 5 rawQuery {
   * """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
   * } searchType SearchType.Scan
   * }}}
   */
  def rawQuery(json: String): this.type = {
    _builder.setSource(json.getBytes("UTF-8"))
    this
  }

  def routing(routing: String): this.type = {
    _builder.setRouting(routing)
    this
  }

  def minScore(minScore: Double): this.type = {
    _builder.setMinScore(minScore.toFloat)
    this
  }

  def preference(pref: String): this.type = {
    _builder.setPreference(pref)
    this
  }

  def indicesOptions(options: IndicesOptions): this.type = {
    _builder.setIndicesOptions(options)
    this
  }

  /**
   * The maximum count for each shard, upon reaching which the query execution will terminate early.
   * If set, the response will have a boolean field terminated_early to indicate whether the query execution
   * has actually terminated_early. Defaults to no terminate_after.
   */
  def terminateAfter(termAfter: Int): this.type = {
    _builder.setTerminateAfter(termAfter)
    this
  }

  def where(string: String): CountDefinition = query(new SimpleStringQueryDefinition(string))
  def where(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
  def query(string: String): CountDefinition = query(new SimpleStringQueryDefinition(string))
  def query(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
  def javaquery(block: => QueryBuilder): CountDefinition = {
    _builder.request.source(new QuerySourceBuilder().setQuery(block))
    //_builder.setQuery(block)
    this
  }

  def types(iterable: Iterable[String]): CountDefinition = types(iterable.toSeq: _*)
  def types(types: String*): CountDefinition = {
    _builder.setTypes(types: _*)
    this
  }
}
