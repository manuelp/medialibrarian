package me.manuelp.medialibrarian;

import fj.data.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.manuelp.medialibrarian.data.Action;
import me.manuelp.medialibrarian.data.Configuration;
import me.manuelp.medialibrarian.data.Tag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static fj.data.List.list;

public class Main {
  public static void main(String[] args) throws IOException,
      InterruptedException {
    Configuration conf = readConfiguration(args);

    System.out.println("----[ MediaLibrarian welcomes you ]----");
    System.out.println("Configuration: " + conf.toString());

    TagsRepository tagsRepository = new SimpleFileTagsRepository(conf
        .getTagsFile().toFile());
    MediaLibrarian librarian = new MediaLibrarian(conf, tagsRepository);

    List<Path> files = librarian.findFiles(conf.getDir());
    System.out.println("Found files: " + files.length());
    processFiles(files, librarian);
  }

  private static Configuration readConfiguration(String[] args) {
    OptionParser parser = new OptionParser();
    OptionSpec<String> fromOption = parser
        .accepts("from", "Path of the root source directory tree.")
        .withRequiredArg().required().ofType(String.class);
    OptionSpec<String> toOption = parser
        .accepts("to", "Path of the archive directory.").withRequiredArg()
        .required().ofType(String.class);
    OptionSpec<Void> help = parser.accepts("help").forHelp();
    OptionSet opts = parser.parse(args);
    if (opts.has(help)) {
      try {
        parser.printHelpOn(System.out);
        System.exit(0);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return new Configuration(Paths.get(fromOption.value(opts)),
        Paths.get(toOption.value(opts)));
  }

  private static void processFiles(List<Path> files, MediaLibrarian librarian)
      throws InterruptedException, IOException {
    for (Path file : files) {
      librarian.showFile(file);
      Action a = readAction("Do you wanna archive it? (y/n/d/q)");
      processAction(file, a, librarian);
    }
  }

  private static Action readAction(String message) {
    System.out.println(message);
    String input = new Scanner(System.in).nextLine();
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
      System.out.println("Sorry, I don't understand what you want.");
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

  private static List<Tag> readTags(String message) {
    System.out.println(message);
    String input = new Scanner(System.in).nextLine();
    return list(input.split(",")).map(Tag::tag);
  }
}
