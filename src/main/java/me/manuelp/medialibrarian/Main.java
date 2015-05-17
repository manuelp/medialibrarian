package me.manuelp.medialibrarian;

import fj.Ord;
import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import fj.function.Effect2;
import jline.console.ConsoleReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.manuelp.medialibrarian.data.Action;
import me.manuelp.medialibrarian.data.Configuration;
import me.manuelp.medialibrarian.data.Tag;
import me.manuelp.medialibrarian.logging.ConsoleLoggerBuilder;
import me.manuelp.medialibrarian.logging.LogLevel;
import me.manuelp.medialibrarian.logging.LoggerBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Main {
  private static ConsoleReader console;
  private static Effect2<String, LogLevel> log;

  public static void main(String[] args) throws IOException,
      InterruptedException {
    Configuration conf = readConfiguration(args);
    console = new ConsoleReader(System.in, System.out);
    LoggerBuilder loggerBuilder = new ConsoleLoggerBuilder(console,
        LogLevel.DEBUG);
    log = loggerBuilder.logger(Main.class);

    console.println("----[ MediaLibrarian welcomes you ]----");
    console.println("Configuration: " + conf.toString());
    console.flush();

    TagsRepository tagsRepository = new SimpleFileTagsRepository(conf
        .getTagsFile().toFile(), loggerBuilder);
    MediaLibrarian librarian = new MediaLibrarian(conf, tagsRepository,
        loggerBuilder);

    if (conf.viewMode()) {
      Set<Tag> tags = tagsRepository.listTags();
      console.println("Available tags: " + formatTags(tags));
      List<Path> files = shuffle(librarian.findByTags(conf.getTagsToView()));
      console.println("Files to view: " + files.length());
      console.flush();
      librarian.showFiles(files);
    } else {
      List<Path> files = shuffle(librarian.findFiles(conf.getDir()));
      console.println("Found files: " + files.length());
      console.flush();
      processFiles(files, librarian);
    }
  }

  private static String formatTags(Set<Tag> tags) {
    return tags.toList().map(Tag::getCode).intersperse(",")
        .foldLeft1((a, b) -> a + b);
  }

  private static Configuration readConfiguration(String[] args) {
    OptionParser parser = new OptionParser();
    OptionSpec<String> fromOption = parser
        .accepts("from", "Path of the root source directory tree.")
        .withRequiredArg().required().ofType(String.class);
    OptionSpec<String> toOption = parser
        .accepts("to", "Path of the archive directory.").withRequiredArg()
        .required().ofType(String.class);
    OptionSpec<Void> help = parser.accepts("help", "Prints this help message.")
        .forHelp();
    OptionSpec<String> view = parser
        .accepts(
          "view",
          "View archived files, optionally filtering them with tags (separated by commas).")
        .withOptionalArg().withValuesSeparatedBy(",");
    OptionSet opts = parser.parse(args);
    if (opts.has(help)) {
      try {
        parser.printHelpOn(System.out);
        System.exit(0);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    Option<Set<Tag>> tagsToView = opts.has(view) ? Option.some(Set.iterableSet(
      Ord.hashOrd(), view.values(opts)).map(Ord.hashOrd(), Tag::tag)) : Option
        .none();

    return new Configuration(Paths.get(fromOption.value(opts)),
        Paths.get(toOption.value(opts)), tagsToView);
  }

  private static <V> List<V> shuffle(List<V> l) {
    java.util.List<V> x = l.toJavaList();
    Collections.shuffle(x);
    return List.iterableList(x);
  }

  private static void processFiles(List<Path> files, MediaLibrarian librarian) {
    files.zipIndex().forEach(
      p -> {
        try {
          console.println(String.format("Processing file %d/%d: %s", p._2(),
            files.length(), p._1().getFileName().toString()));
          librarian.showFile(p._1());
          Action a = readAction("Do you wanna archive it? (y/n/d/q) ");
          processAction(p._1(), a, librarian);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
  }

  private static Action readAction(String message) throws IOException {
    String input = console.readLine(message);
    switch (input) {
    case "y":
      return Action.ARCHIVE;
    case "n":
      return Action.SKIP;
    case "d":
      return Action.DELETE;
    case "q":
      return Action.QUIT;
    default:
      console.println("Sorry, I don't understand what you want.");
      return readAction("Do you wanna archive it? (y/n/d/q)");
    }
  }

  private static void processAction(Path file, Action a,
      MediaLibrarian librarian) {
    switch (a) {
    case ARCHIVE:
      librarian.archive(file,
        readTags("Gimmie some tags, separated with commas: "));
    case DELETE:
      file.toFile().delete();
    case SKIP:
      break;
    case QUIT:
      System.exit(0);
    }
  }

  private static Set<Tag> readTags(String message) {
    String input;
    try {
      input = console.readLine(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Set.set(Ord.stringOrd, input.split(","))
        .map(Ord.hashOrd(), Tag::tag);
  }
}
