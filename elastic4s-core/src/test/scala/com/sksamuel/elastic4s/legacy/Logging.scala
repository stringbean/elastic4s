package com.sksamuel.elastic4s.legacy

import org.slf4j.LoggerFactory

/** @author Stephen Samuel */
trait Logging {
  val logger = LoggerFactory.getLogger(getClass)
}
