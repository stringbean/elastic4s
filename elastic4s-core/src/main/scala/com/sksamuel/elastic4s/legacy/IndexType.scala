package com.sksamuel.elastic4s.legacy

import scala.language.implicitConversions

/** @author Stephen Samuel */
case class IndexType(index: String, `type`: String)
case class IndexesTypes(indexes: Seq[String], types: Seq[String]) {
  def index = indexes.headOption.getOrElse(throw new RuntimeException("Specify at least one index"))
  def typ = types.headOption
}
object IndexesTypes {
  def apply(indexes: Iterable[String]): IndexesTypes = indexes.size match {
    case 0 => throw new RuntimeException("Could not parse into index/type")
    case 1 => apply(indexes.head)
    case _ => apply(indexes.toSeq, Nil)
  }
  def apply(tuple: (String, String)): IndexesTypes = apply(tuple._1, tuple._2)
  def apply(index: String, `type`: String): IndexesTypes = IndexesTypes(List(index), List(`type`))
  def apply(indexType: IndexType): IndexesTypes = IndexesTypes(indexType.index, indexType.`type`)
  def apply(string: String): IndexesTypes = {
    string.split("/") match {
      case Array(index) => IndexesTypes(Array(index), Nil)
      case Array(index, t) => IndexesTypes(List(index), List(t))
      case _ => throw new RuntimeException("Could not parse into index/type")
    }
  }
}
trait IndexesTypesDsl {
  implicit def string2indexestypes(string: String): IndexesTypes = IndexesTypes(string)
  implicit def tuple2indexestypes(tuple: (String, String)): IndexesTypes = IndexesTypes(tuple)
}
