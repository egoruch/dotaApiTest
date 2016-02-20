package com.eb.schedule.model.services

import com.eb.schedule.model.dao.ScheduledGameRepComp
import com.eb.schedule.model.slick.{LiveGame, ScheduledGame}

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait ScheduledGameServiceComponent {

  def scheduledGameService: ScheduledGameService

  trait ScheduledGameService {
    def findById(id: Int): Future[ScheduledGame]

    def findByMatchId(matchId: Long): Future[ScheduledGame]

    def exists(id: Int): Future[Boolean]

    def insert(game: ScheduledGame): Future[Int]

    def update(game: ScheduledGame): Future[Int]

    def updateStatus(id: Int, status: Byte): Future[Int]

    def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int]

    def delete(id: Int): Future[Int]

    def getScheduledGames(matchDetails: LiveGame): Future[ScheduledGame]
  }

}

trait ScheduledGameServiceImplComponent extends ScheduledGameServiceComponent {
  this: ScheduledGameRepComp =>

  def scheduledGameService = new ScheduledGameServiceImpl

  class ScheduledGameServiceImpl extends ScheduledGameService {
    def findById(id: Int): Future[ScheduledGame] = {
      repository.findById(id)
    }

    def findByMatchId(matchId: Long): Future[ScheduledGame] = {
      repository.findByMatchId(matchId)
    }

    def exists(id: Int): Future[Boolean] = {
      repository.exists(id)
    }

    def insert(game: ScheduledGame): Future[Int] = {
      repository.insert(game)
    }

    def update(game: ScheduledGame): Future[Int] = {
      repository.update(game)
    }

    def updateStatus(id: Int, status: Byte): Future[Int] = {
      repository.updateStatus(id, status)
    }

    def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int] = {
      repository.updateScore(matchId, radiantScore, direScore)
    }

    def delete(id: Int): Future[Int] = {
      repository.delete(id)
    }

    def getScheduledGames(liveGame: LiveGame): Future[ScheduledGame] ={
      repository.getScheduledGames(liveGame)
    }
  }

}