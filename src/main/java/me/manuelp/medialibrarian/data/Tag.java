package me.manuelp.medialibrarian.data;

import fj.Ord;
import me.manuelp.medialibrarian.validations.Checks;

public class Tag {
  private final String code;

  public Tag(String code) {
    Checks.notNull("Tag code", code);
    this.code = code;
  }

  public static Tag tag(String code) {
    return new Tag(code);
  }

  public static Ord<Tag> ord() {
    return Ord.hashOrd();
  }

  public String getCode() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Tag tag = (Tag) o;

    return getCode().equals(tag.getCode());

  }

  @Override
  public int hashCode() {
    return getCode().hashCode();
  }

  @Override
  public String toString() {
    return "Tag{" + "code='" + code + '\'' + '}';
  }
}
