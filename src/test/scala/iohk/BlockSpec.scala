package iohk

import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike

class BlockSpec extends AsyncWordSpecLike with Matchers with OptionValues {

  "A Block" when {
    ".cryptoHash" must {
      "return a Hash object of the hash value of  the entire block" in {
        Mining.Genesis
          .map { blk =>
            blk.cryptoHash mustBe a[Hash]

          }
      }

      ".verifyThisHasBeenMinedProperly" must {
        "verify a properly mined block by comparing the cryptoHash and the mining target number" in {
          Mining.Genesis
            .map { blk =>
              val stdMnNum = Mining.StdMiningTargetNumber
              blk.cryptoHash.toNumber mustBe <(stdMnNum)
            }
        }
      }

    }

  }
}
