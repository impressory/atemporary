package com.assessory.api.critique

import com.wbillingsley.handy._
import com.assessory.api._
import com.assessory.api.Task
import com.wbillingsley.handy.appbase.{Answer, Question, GroupSet}

/**
  * A critique is posted in response to something
  */
case class Critique(
  target: Target,

  task: TaskOutputBody
) extends TaskOutputBody {
  val kind = CritiqueTask.kind
}

abstract class CritTargetStrategy extends HasKind

case class MyOutputStrategy(
  task: Id[Task,String]
) extends CritTargetStrategy {
  val kind = MyOutputStrategy.kind
}

object MyOutputStrategy {
  val kind = "outputs relevant to me"
}

case class OfMyGroupsStrategy(
  task: Id[Task,String]
) extends CritTargetStrategy {
  val kind = OfMyGroupsStrategy.kind
}
case object OfMyGroupsStrategy {
  val kind = "critiques of my groups"
}

case class PreallocateGroupStrategy(
  set: Id[GroupSet, String],
  number: Int
) extends CritTargetStrategy {
  val kind = PreallocateGroupStrategy.kind
}

case class AllocateStrategy(
  what: TargetType,
  number: Int
)

case class AnyStrategy(
  what: TargetType,
  number: Int
)

object PreallocateGroupStrategy {
  val kind = "group"
}


case class CritiqueTask (
  strategy: CritTargetStrategy,
  task: TaskBody
) extends TaskBody {
  val kind = CritiqueTask.kind
}

object CritiqueTask {
  val kind = "Critique"
}


case class AllocatedCrit(
  target: Target,

  critique: Option[Id[TaskOutput, String]] = None
)


case class CritAllocation(

  id: Id[CritAllocation,String],

  task: Id[Task, String],

  completeBy: Target,

  allocation: Seq[AllocatedCrit] = Seq.empty

) extends HasStringId[CritAllocation]
