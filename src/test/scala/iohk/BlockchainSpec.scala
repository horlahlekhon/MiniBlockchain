package iohk

import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class BlockchainSpec extends AsyncWordSpecLike with Matchers with OptionValues {

  "FastBlockchain" when {
    ".apply" must {
      "Initialize the chain with a Genesis block" in {
        val chain = FastBlockchain()
        chain.map { cha =>
          val genesis = cha.findByIndex(0)
          genesis.value.parentHash.toNumber mustBe Sha256.Zero_Hash.toNumber
        }
      }
    }
    ".findByIndex" must {
      "Find an element by its index" in {
        val chain = FastBlockchain()
        chain.map { cha =>
          val initialBlk = cha.findByIndex(0)
          initialBlk mustBe defined
          initialBlk.value.index mustBe 0
          initialBlk.value.cryptoHash mustBe a[Hash]
        }

      }
    }
    ".append" must {
      "add a new block to the chain" in {
        val chain = FastBlockchain()
        chain map { chain =>
          val blk = Await.result(Mining.mineNextBlock(chain.lastBlock.index + 1, chain.lastBlock.cryptoHash, Seq(Transaction("Rice")), Mining.StdMiningTargetNumber), Inf)
          val app: FastBlockchain = chain.append(blk).asInstanceOf[FastBlockchain]
          val second = app.findByIndex(1)
          val first = chain.findByIndex(0).value
          second.value.parentHash.toNumber mustBe first.cryptoHash.toNumber
          chain.length mustBe 2
          app.length mustBe 2
        }
      }
      "Reject appending a block with invalid index or hash" in {
        val chain = FastBlockchain()
        chain.map { chain =>
          assertThrows[BadlyCreatedBlock] {
            val initialBlk = chain.lastBlock
            val blk = new Block(index = 5, parentHash = initialBlk.cryptoHash, transactions = Vector(Transaction("This is a transaction")), Mining.StdMiningTargetNumber, Mining.targetByLeadingZeros(3).toLong)
            chain.append(blk)
          }
        }
      }
    }
    ".findByHash" must {
      "Find an element by its Hash" in {
        val chain = FastBlockchain()
        chain.map { chain =>
          val last = chain.lastBlock
          val blk = Await.result(Mining.mineNextBlock(chain.lastBlock.index + 1, chain.lastBlock.cryptoHash, Seq(Transaction("Rice")), Mining.StdMiningTargetNumber), Inf)
          val app = chain.append(blk)
          val res = app.findByHash(blk.cryptoHash)
          res mustBe defined
          res.value.index.mustBe(1)
        }
      }
    }
  }
}
