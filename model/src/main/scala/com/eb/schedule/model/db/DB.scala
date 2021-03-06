package com.eb.schedule.model.db

import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

/**
  * Created by Iegor.Bondarenko on 28.04.2017.
  */
trait DB {
  def dbConfig: DatabaseConfig[JdbcProfile]

  implicit val  db: JdbcBackend#DatabaseDef = dbConfig.db
}
