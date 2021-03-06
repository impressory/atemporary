package com.assessory.model

import com.assessory.api.wiring.Lookups._
import com.assessory.asyncmongo._
import com.wbillingsley.handy.Ref._
import com.wbillingsley.handy._
import com.wbillingsley.handy.appbase.{Identity, User, ActiveSession, UserError}

object UserModel {

  /**
   * Creates a user and logs them in
   */
  def signUp(oEmail:Option[String], oPassword:Option[String], session:ActiveSession) = {
    for (
      email <- oEmail.toRef orFail UserError("Email must not be blank");
      password <- oPassword.toRef orFail UserError("Password must not be blank");
      user <- {
        val u = UserDAO.unsaved
        val set = u.copy(
          pwlogin=u.pwlogin.copy(email=Some(email), pwhash=Some(UserDAO.hash(password))),
          activeSessions=Seq(session)
        )
        UserDAO.saveNew(set)
      }
    ) yield user
  }

  def byEmail(email:String) = UserDAO.byEmail(email)

  def byIdentity(id:Identity) = UserDAO.byIdentity(id)

  /**
   * Logging a user in involves finding the user (if the password hash matches), and pushing the
   * current session key as an active session
   */
  def logIn(oEmail:Option[String], oPassword:Option[String], session:ActiveSession) = {
    for {
      email <- oEmail.toRef orFail UserError("Email must not be blank")
      password <- oPassword.toRef orFail UserError("Password must not be blank")
      user <- UserDAO.byEmailAndPassword(email, password)
      updated <- UserDAO.pushSession(user.itself, session)
    } yield updated
  }

  /**
   * Logs a user in using their system-set secret
   */
  def secretLogIn(ru:Ref[User], secret:String, activeSession:ActiveSession) = {
    for {
      oldUser <- UserDAO.deleteSession(ru, activeSession).toRefOpt
      u <- ru if u.secret == secret
      pushed <- UserDAO.pushSession(u.itself, activeSession)
    } yield pushed
  }

  /**
   * To log a user out, we just have to remove the current session from their active sessions
   */
  def logOut(rUser:Ref[User], session:ActiveSession) = {
    for (
      u <- rUser;
      user <- UserDAO.deleteSession(u.itself, session)
    ) yield {
      user
    }
  }

  def findMany(a:Approval[User], ids:Ids[User,String]) = {
    ids.lookUp
  }

  def displayName(u:User):String = {
    u.name.orElse(u.pwlogin.email.orElse(u.identities.find(_.username.nonEmpty).flatMap(_.username))).getOrElse("Unnamed user")
  }

}
