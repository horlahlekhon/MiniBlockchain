package iohk

import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class MiningSpec extends AsyncWordSpecLike with Matchers with OptionValues {


  "Mining" when {
    ".mineNextBlock" must {
      "Mine A block given passed in inputs" in {
        FastBlockchain()
          .map { chain =>
            val blk = Await.result(Mining.mineNextBlock(chain.lastBlock.index + 1, chain.lastBlock.cryptoHash, Seq(Transaction("Rice")), Mining.StdMiningTargetNumber), Inf)
            blk.parentHash.toNumber mustEqual chain.lastBlock.cryptoHash.toNumber
            blk.index mustEqual chain.lastBlock.index + 1
          }
      }
    }
  }
}
