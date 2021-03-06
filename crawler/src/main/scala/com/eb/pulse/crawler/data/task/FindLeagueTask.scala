package com.eb.pulse.crawler.data.task

import com.eb.pulse.crawler.CrawlerUrls._
import com.eb.pulse.crawler.data.service.{LeagueService, TaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

//todo i guess i forgot to refactor this tas
class FindLeagueTask(leagueService: LeagueService, taskService: TaskService, httpUtils: HttpUtils) extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    try {
      val tasks: Future[Seq[UpdateTask]] = taskService.getPendingLeagueTasks()
      val result: Seq[UpdateTask] = Await.result(tasks, Duration.Inf)
      val ids: Seq[Long] = result.map(_.id)
      val steamItems: JsonArray = getItemsInfoFromSteam()
      for (i <- 0 until steamItems.size()) {
        val itemJson: JsonObject = steamItems.get(i).getAsJsonObject

        val leagueId: Int = itemJson.get("leagueid").getAsInt
        if (ids.contains(leagueId)) {
          log.debug("find league for saving: {}", itemJson)
          leagueService.insert(new League(leagueId, parseName(itemJson.get("name").getAsString), url = Some(itemJson.get("tournament_url").getAsString)))
          taskService.update(new UpdateTask(leagueId, League.getClass.getSimpleName, 1))
        }
      }
    } catch {
      case e: Throwable => log.error("Issue in running task", e)
    }
  }


  def parseName(name: String): String = {
        name.replace("#DOTA_Item_", "").replace("_", " ")
  }


  def getItemsInfoFromSteam(): JsonArray = {
    val teamInfo: JsonObject = httpUtils.getResponseAsJson(GET_LEAGUES)
    val result: JsonObject = teamInfo.getAsJsonObject("result")
    if(result != null){
      val items: JsonArray = result.getAsJsonArray("leagues")
      items
    }else{
      new JsonArray
    }

  }

}
