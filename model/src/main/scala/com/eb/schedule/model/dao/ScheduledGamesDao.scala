package com.eb.schedule.model.dao

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.slick._
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
object ScheduledGamesDao extends DBConf {


  lazy val games = new TableQuery(tag => new ScheduledGames(tag))


  def filterQuery(id: Int): Query[ScheduledGames, ScheduledGame, Seq] = games.filter(_.id === id)

  def findById(id: Int): Future[ScheduledGame] =
    try db.run(filterQuery(id).result.head)
    finally db.close

  def findByMatchId(matchId: Long): Future[ScheduledGame] =
    try db.run(games.filter(_.matchId === matchId).result.head)
    finally db.close

  def exists(id: Int): Future[Boolean] =
    try db.run(filterQuery(id).exists.result)
    finally db.close

  def insert(game: ScheduledGame): Future[Int] = {
    try db.run(games += game)
    finally db.close
  }

  def update(game: ScheduledGame): Future[Int] = {
    try db.run(filterQuery(game.id).update(game))
    finally db.close
  }

  def delete(id: Int): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close


  private def getScheduledGameQuery(liveGame: LiveGame) = {
    games.filter(g => g.status === MatchStatus.SCHEDULED.status && g.leagueId === liveGame.leagueId && ((g.radiant === liveGame.radiant && g.dire === liveGame.dire) || (g.radiant === liveGame.dire && g.dire === liveGame.radiant)))
      .sortBy(_.startDate)
  }

  def getScheduledGames(matchDetails: LiveGame):Future[ScheduledGame] = {
    try db.run(getScheduledGameQuery(matchDetails).result.head)
    finally db.close
  }
}
