package me.manuelp.medialibrarian.data;

import fj.F;
import me.manuelp.medialibrarian.validations.Checks;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static fj.P.p;

public class Hash {
  private final String hash;

  public Hash(String hash) {
    Checks.notNull(p("Hash code", hash));
    this.hash = hash;
  }

  public static Hash hash(String hash) {
    return new Hash(hash);
  }

  public static F<Path, Hash> calculateHash() {
    return p -> {
      try {
        return hash(DigestUtils.md5Hex(new FileInputStream(p.toFile())));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public String getString() {
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Hash hash1 = (Hash) o;

    return getString().equals(hash1.getString());

  }

  @Override
  public int hashCode() {
    return getString().hashCode();
  }

  @Override
  public String toString() {
    return "Hash{" + "hash='" + hash + '\'' + '}';
  }
}
