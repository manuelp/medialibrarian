package me.manuelp.medialibrarian;

import me.manuelp.medialibrarian.validations.Checks;

import java.nio.file.Path;

import static fj.P.p;

public class Configuration {
  private final Path dir, archive;

  public Configuration(Path dir, Path archive) {
    Checks.notNull(p("Source path", dir), p("Archive path", archive));
    this.dir = dir;
    this.archive = archive;
  }

  public Path getDir() {
    return dir;
  }

  public Path getArchive() {
    return archive;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Configuration that = (Configuration) o;

    if (!getDir().equals(that.getDir()))
      return false;
    return getArchive().equals(that.getArchive());

  }

  @Override
  public int hashCode() {
    int result = getDir().hashCode();
    result = 31 * result + getArchive().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Configuration{" + "dir=" + dir + ", archive=" + archive + '}';
  }
}
