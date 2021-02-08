package iohk

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf

object Main extends App {
  val chain = Await.result(FastBlockchain(), Inf)
}
