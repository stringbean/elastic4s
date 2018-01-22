package com.sksamuel.elastic4s.legacy.mappings

import com.sksamuel.elastic4s.legacy.{ EnglishLanguageAnalyzer, SpanishLanguageAnalyzer, JsonSugar }
import com.sksamuel.elastic4s.legacy.mappings.FieldType.{ StringType, GeoPointType, DateType }
import org.scalatest.{ Matchers, WordSpec }

class MappingDefinitionDslTest extends WordSpec with Matchers with JsonSugar {

  import com.sksamuel.elastic4s.legacy.ElasticDsl._

  "mapping definition" should {
    "insert source exclusion directives when set" in {
      val mapping = new MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2")
      val output = mapping.build.string()
      println("output = " + output)
      output should include (""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
    }
    "insert source exclusion directives when set and override enabled directive" in {
      val mapping = new MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2").source(true)
      val output = mapping.build.string()
      println("output2 = " + output)
      output should include (""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
      output should not include """"enabled":true"""
    }
    "insert source enabling" in {
      val mapping = new MappingDefinition("test").source(false)
      val output = mapping.build.string()
      println("output2 = " + output)
      output should not include """"_source":{"excludes":["excludeMe1","excludeMe2"]}"""
      output should include (""""enabled":false""")
    }
    "not insert date detection by default" in {
      val mapping = new MappingDefinition("type")
      val output = mapping.build.string()
      output should not include "date"
    }
    "insert date detection when set to true" in {
      val mapping = new MappingDefinition("type").dateDetection(true)
      val output = mapping.build.string()
      output should include("""date_detection":true""")
    }
    "insert date detection when set to false" in {
      val mapping = new MappingDefinition("type").dateDetection(false)
      val output = mapping.build.string()
      output should include("""date_detection":false""")
    }
    "not insert numeric detection by default" in {
      val mapping = new MappingDefinition("type")
      val output = mapping.build.string()
      output should not include "numeric"
    }
    "insert numeric detection when set to true" in {
      val mapping = new MappingDefinition("type").numericDetection(true)
      val output = mapping.build.string()
      output should include("""numeric_detection":true""")
    }
    "insert numeric detection when set to false" in {
      val mapping = new MappingDefinition("type").numericDetection(false)
      val output = mapping.build.string()
      output should include("""numeric_detection":false""")
    }
    "include dynamic templates" in {
      val req = create.index("docsAndTags").mappings(
        mapping name "my_type" templates (
          template name "es" matching "*_es" matchMappingType "string" mapping {
            field typed StringType analyzer SpanishLanguageAnalyzer
          } matchPattern "regex",
          template name "en" matching "*" matchMappingType "string" mapping {
            field typed StringType analyzer EnglishLanguageAnalyzer
          }
        )
      )
      req._source.string should matchJsonResource("/json/mappings/mappings_with_dyn_templates.json")
    }
    "include timestamp path field" in {
      val req = create.index("docsAndTags").mappings(
        mapping name "foo" timestamp {
          timestamp enabled true path "mypath" store true
        }
      )
      req._source.string should matchJsonResource("/json/mappings/timestamp_path.json")
    }
    "include timestamp store field" in {
      val req = create.index("docsAndTags").mappings(
        mapping name "foo" timestamp {
          timestamp enabled true path "mypath" store true
        }
      )
      req._source.string should matchJsonResource("/json/mappings/timestamp_store.json")
    }
    "include timestamp format field" in {
      val req = create.index("docsAndTags").mappings(
        mapping name "foo" timestamp {
          timestamp enabled true format "qwerty"
        }
      )
      req._source.string should matchJsonResource("/json/mappings/timestamp_format.json")
    }
    "include honor disabled timestamp" in {
      val req = create.index("docsAndTags").mappings(
        mapping name "foo" timestamp {
          timestamp(false)
        }
      )
      req._source.string should matchJsonResource("/json/mappings/timestamp_disabled.json")
    }
  }
}
