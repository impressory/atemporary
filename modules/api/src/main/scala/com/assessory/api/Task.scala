package com.assessory.api

import com.assessory.api.due.{NoDue, Due}
import com.wbillingsley.handy.{Ids, Id, HasStringId, HasKind}
import com.wbillingsley.handy.appbase._

/**
  * There could be many different kinds of task -- questionnaires to fill, essays to write. We implement these
  * as `TaskBody`s
  */
trait TaskBody

/**
  * A sentinel for when a TaskBody has not been created
  */
object EmptyTaskBody extends TaskBody

/**
  * A task is something that someone has to do.
  */
case class Task(

  id: Id[Task,String],

  course: Id[Course, String],

  details: TaskDetails = new TaskDetails(),

  body: TaskBody = EmptyTaskBody

) extends HasStringId[Task]


case class TaskDetails (

  name:Option[String] = None,

  description:Option[String] = None,

  created: Long = System.currentTimeMillis,

  groupSet: Option[Id[GroupSet, String]] = None,

  individual: Boolean = true,

  published: Due = NoDue,

  open: Due = NoDue,

  due: Due = NoDue,

  closed: Due = NoDue

)



