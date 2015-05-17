package me.manuelp.medialibrarian.data;

import fj.data.Set;
import me.manuelp.medialibrarian.validations.Checks;

import java.nio.file.Path;

import static fj.P.p;

public class MediaFile {
  private final Hash hash;
  private final Path path;
  private final Set<Tag> tags;

  public MediaFile(Path path, Set<Tag> tags) {
    this(Hash.calculateHash().f(path), path, tags);
  }

  public MediaFile(Hash hash, Path path, Set<Tag> tags) {
    Checks.notNull(p("Hash", hash), p("Path", path), p("Tags", tags));
    this.hash = hash;
    this.path = path;
    this.tags = tags;
  }

  public Hash getHash() {
    return hash;
  }

  public Path getPath() {
    return path;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    MediaFile mediaFile = (MediaFile) o;

    if (!getHash().equals(mediaFile.getHash()))
      return false;
    if (!getPath().equals(mediaFile.getPath()))
      return false;
    return getTags().equals(mediaFile.getTags());

  }

  @Override
  public int hashCode() {
    int result = getHash().hashCode();
    result = 31 * result + getPath().hashCode();
    result = 31 * result + getTags().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "MediaFile{" + "hash=" + hash + ", path=" + path + ", tags=" + tags
        + '}';
  }
}
