package com.sksamuel.elastic4s.legacy

import com.sksamuel.elastic4s.legacy.source.Indexable

package object examples {
  implicit case object DocumentIndexable extends Indexable[Document] {
    override def json(t: Document): String = ???
  }
}
