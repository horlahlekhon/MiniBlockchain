package iohk

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf

object Main extends App {
  val chain = Await.result(FastBlockchain(), Inf)
  val chain2 = Await.result(FastBlockchain(), Inf)

  println(chain.common_ancestor(chain2))
}
