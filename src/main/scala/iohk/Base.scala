package iohk

import iohk.Base.Bytes


object Base {
  type Unknown = Array[Block]


  type Nonce = Long


  type Bytes = Array[Byte]

  type Number = BigInt

  //takes an int and return the bytes iArray
  val Bytes = new Array[Byte](_: Int)

  val Number: BigInt.type = BigInt


  def toHexString(bytes: Array[Byte]): String =
    "0x" + bytes.map(b => String.format("%02X", Byte.box(b))).mkString("")

}

// The idea behind any cryptographic hash representation in "mini-chain"
// is to treat it as an immutable array of bytes that can be also viewed
// as a number or a hex string. You will see that the number representation
// is used in the mining process. The hex representation is for logging
// purposes.
case class Hash(bytes: Bytes) {

  import Base._

  def toHexString: String = Base.toHexString(bytes)

  override def toString: String = s"iohk.Hash($toNumber)"

  def toNumber: Number = Number(1, bytes)

}
