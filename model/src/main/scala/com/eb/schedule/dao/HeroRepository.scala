package com.eb.schedule.dao

import com.eb.schedule.model.slick.Hero
import com.eb.schedule.model.slick.Hero.HeroTable
import slick.jdbc.JdbcBackend
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

/**
  * Created by Egor on 26.03.2016.
  */
trait HeroRepository {

  def findById(id: Int): Future[Hero]

  def exists(id: Int): Future[Boolean]

  def findAll(): Future[Seq[Hero]]
}

class HeroRepositoryImpl (implicit db: JdbcBackend#DatabaseDef) extends HeroRepository {

  lazy val heroes = Hero.table

  def filterQuery(id: Int): Query[HeroTable, Hero, Seq] = heroes.filter(_.id === id)

  def findById(id: Int): Future[Hero] =
    db.run(filterQuery(id).result.head)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def findAll(): Future[Seq[Hero]] = {
    db.run(heroes.map(h => h).result)
  }

  def insert(hero: Hero): Future[Int] = {
    db.run(heroes += hero)
  }
}
