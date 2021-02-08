package iohk

import java.security.MessageDigest

import iohk.Base._

object Sha256 {

  val NumberOfBytes = 32

  val TheDigest: MessageDigest = MessageDigest.getInstance("SHA-256")

  /*We pre-compute the hash of an empty array of 32 bytes.
  We call this the "Zero_Hash".*/
  TheDigest.update(Bytes(32))

  val Zero_Hash: Hash = Hash(TheDigest.digest())


  // We use this to hash a composite structure whose constituents can be given
  // as byte arrays. We just feed everything to SHA-256.
  def apply(bytess: Bytes*): Hash = {
    synchronized {
      for (bytes <- bytess) {
        TheDigest.update(bytes)
      }
      val hash = TheDigest.digest()
      assert(hash.length == NumberOfBytes)
      Hash(hash)
    }

  }

}