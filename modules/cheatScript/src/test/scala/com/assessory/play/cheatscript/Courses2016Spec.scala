package com.assessory.play.cheatscript


import com.assessory.api.{Task, TaskDetails}
import com.assessory.asyncmongo.{DB, RegistrationDAO, UserDAO}
import com.assessory.model.{DoWiring, TaskModel, CourseModel, UserModel}
import com.wbillingsley.handy.{Approval, Ref}
import com.wbillingsley.handy.appbase.{LTIConsumer, ActiveSession, Course, User}
import com.wbillingsley.handy.Id._
import Ref._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

class Courses2016Spec extends FlatSpec with Matchers with ScalaFutures {

  "setting up the courses" should "succeed" in {

    DB.dbName = "assessory_2016_1"
    // Wire up the lookups
    DoWiring.doWiring

    val proc = for {
      will <- UserModel.signUp(
        Some("wbilling@une.edu.au"),
        Some("Aalurfwayaf"),
        ActiveSession("locally made", "127.0.0.1")
      )

      testCourseWp <- CourseModel.create(
        Approval(will.itself), Course(
          id = "570fa8c4470b6bd820000000".asId,
          addedBy = "invalid".asId,
          title = Some("test course"),
          shortName = Some("TEST101"),
          ltis = Seq(LTIConsumer("UNE moodle", "grumplestiltskin", Some("Term 1 at UNE")))
        )
      )

      videoTask <- TaskModel.create(Approval(will.itself),
        Task(
          id = "invalid".asId,
          course = testCourseWp.item.id,
          details = TaskDetails()
        )
      )
    } yield "success"

    proc.toFuture.futureValue should be ("success")

  }

}
