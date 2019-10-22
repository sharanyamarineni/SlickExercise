package slick
import slick.dbio.{DBIOAction, Effect}
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.QueryBase
import slick.sql.FixedSqlAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object EmployeeDetails extends App {
  case class Employee(tag:Tag) extends Table[(Int,String,Int,Int)](tag,"EMPLOYEE"){
    def id = column[Int]("ID")
    def name= column[String]("NAME")
    def age= column[Int]("AGE")
    def depId= column[Int]("DEP_ID")

    def * = (id,name,age,depId)
  }
  val employee= TableQuery(Employee)

  case class Department(tag:Tag) extends Table[(Int,String,String)](tag,"DEPARTMENT"){
    def id= column[Int]("ID")
    def depName=column[String]("DEP_NAME")
    def depLocation=column[String]("DEP_LOCATION")

    def * = (id,depName,depLocation)

//    def employeeFk = foreignKey("Emp_FK",id,employee)(_.id)
    }
  val department= TableQuery(Department)

  try{

    val res2: Unit =Await.result(DatabaseConnection.db.run(DBIO.seq(
      employee.schema.create
    )), Duration.Inf)

    val insert = {
      employee.map(e => (e.id,e.name,e.age,e.depId)) += (1, "john", 26, 3)
    }

    val update = {
      employee.filter(_.name === "john")
        .map(p => (p.name,p.age))
        .update(("john",29))
    }
    val delete = {
      employee.filter(e => e.name === "john")
        .delete
    }

    val insertResult = Await.result(DatabaseConnection.db.run(insert), Duration.Inf)
    val updateResult = Await.result(DatabaseConnection.db.run(update), Duration.Inf)
    val deleteResult = Await.result(DatabaseConnection.db.run(delete), Duration.Inf)
  }finally DatabaseConnection.db.close




//    try {
//    val set: DBIOAction[Unit, NoStream, Effect.Schema with Effect.Write] = DBIO.seq(
//      (employee.schema ++ department.schema).create,
//      employee += (1, "john", 26, 3),
//      employee += (2, "sony", 30, 4),
//      employee += (3, "peter", 40, 2),
//      employee += (4, "marvel", 22, 3),
//      employee += (5, "kim", 50, 4),
//      employee += (6, "bill", 45, 2),
//      employee += (6, "james", 29, 4),
//
//      department ++= Seq(
//        (5, "IT", "hyderabad"),
//        (2, "IT", "bangalore"),
//        (3, "IT", "chennai"),
//        (1, "HR", "hyderabad"),
//        (4, "HR", "chennai"),
//        (6, "HR", "bangalore")
//      )
//    )
//    val setupFuture: Future[Unit] = DatabaseConnection.db.run(set)
//
//    println("DepNames:")
//    val dep= department.map(p => (p.id, p.depName, p.depLocation))
//    val results: Future[Seq[(Int, String, String)]] =DatabaseConnection.db.run(dep.result)
//    println(results.map(r=>(r)))
//
//
//    employee.filter(x => x.name === "john")
//      .map(p => (p.name, p.age))
//      .update(("john", 23))
//
//    employee.filter(x => x.name === "james").delete
//
//    println("employee details")
//    val emp = employee.map(e => (e.id, e.name, e.age, e.depId))
//    DatabaseConnection.db.run(employee.result)
//  }finally DatabaseConnection.db.close()
}

