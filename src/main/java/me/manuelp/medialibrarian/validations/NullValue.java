package me.manuelp.medialibrarian.validations;

class NullValue implements ValidationError {
  private final String name;

  public NullValue(String name) {
    if (name == null)
      throw new IllegalArgumentException("Null value name cannot be null!");

    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getMessage() {
    return name + " cannot be null!";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    NullValue nullValue = (NullValue) o;

    return getName().equals(nullValue.getName());

  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public String toString() {
    return "NullValue{" + "name='" + name + '\'' + '}';
  }
}
