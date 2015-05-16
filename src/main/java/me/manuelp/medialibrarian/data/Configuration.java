package me.manuelp.medialibrarian.data;

import fj.data.Option;
import fj.data.Set;
import me.manuelp.medialibrarian.validations.Checks;

import java.nio.file.Path;
import java.nio.file.Paths;

import static fj.P.p;

public class Configuration {
  private final Path dir, archive;
  private Option<Set<Tag>> tagsToView;

  public Configuration(Path dir, Path archive, Option<Set<Tag>> tagsToView) {
    Checks.notNull(p("Source path", dir), p("Archive path", archive),
      p("Tags to view", tagsToView));
    this.dir = dir;
    this.archive = archive;
    this.tagsToView = tagsToView;
  }

  public Path getDir() {
    return dir;
  }

  public Path getArchive() {
    return archive;
  }

  public Path getTagsFile() {
    return Paths.get(archive.toString(), "tags.tgs");
  }

  public boolean viewMode() {
    return tagsToView.isSome();
  }

  public Option<Set<Tag>> getTagsToView() {
    return tagsToView;
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
    if (!getArchive().equals(that.getArchive()))
      return false;
    return getTagsToView().equals(that.getTagsToView());

  }

  @Override
  public int hashCode() {
    int result = getDir().hashCode();
    result = 31 * result + getArchive().hashCode();
    result = 31 * result + getTagsToView().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Configuration{" + "dir=" + dir + ", archive=" + archive
        + ", tagsToView=" + tagsToView + '}';
  }
}
