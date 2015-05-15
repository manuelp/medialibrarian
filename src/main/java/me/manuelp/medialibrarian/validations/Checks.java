package me.manuelp.medialibrarian.validations;

import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.Option;

import static fj.data.List.list;

public class Checks {
  public static void notNull(String what, Object o) {
    if (o == null)
      throw new RuntimeException(what + " cannot be null!");
  }

  public static void notNull(P2<String, ?>... thing) {
    List<String> errors = Option.somes(list(thing).map(checkIfValueIsNull())
        .map(o -> o.map(ValidationError::getMessage)));
    if (errors.isNotEmpty())
      throw new IllegalArgumentException(errors.intersperse(" / ").foldLeft(
        (a, b) -> a + b, ""));
  }

  private static F<P2<String, ?>, Option<ValidationError>> checkIfValueIsNull() {
    return t -> {
      if (t._2() == null)
        return Option.<ValidationError> some(new NullValue(t._1()));
      else
        return Option.<ValidationError> none();
    };
  }

}
