package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend

/**
  * Created by Egor on 20.02.2016.
  */
object H2DB extends DB{
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("h2")
  def db: JdbcBackend#DatabaseDef = dbConfig.db
  import com.github.tototoshi.slick.H2JodaSupport._
}
