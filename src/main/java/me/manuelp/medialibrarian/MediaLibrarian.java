package me.manuelp.medialibrarian;

import fj.data.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static fj.data.List.list;

public class MediaLibrarian {
  private final static List<String> EXTENSIONS = list("mp4", "flv", "avi",
    "mpg", "webm", "wmv");
  private static final String VIDEO_PLAYER = "vlc";
  private static Configuration conf;

  public static void main(String[] args) throws IOException,
      InterruptedException {
    conf = readConfiguration(args);

    System.out.println("----[ MediaLibrarian welcomes you ]----");
    System.out.println("Configuration: " + conf.toString());

    List<Path> files = shuffle(findInterestingFiles(conf.getDir()));
    System.out.println("Found files: " + files.length());

    processFiles(files);
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

  private static void processFiles(List<Path> files)
      throws InterruptedException, IOException {
    for (Path file : files) {
      showFile(file);
      Action a = readAction("Do you wanna archive it? (y/n/d/q)");
      processAction(file, a);
    }
  }

  private static void processAction(Path file, Action a) {
    switch (a) {
    case ARCHIVE:
      archive(file);
    case DELETE:
      file.toFile().delete();
    case SKIP:
      break;
    case QUIT:
      System.exit(0);
    }
  }

  private static void archive(Path file) {
    List<Tag> tags = readTags("Gimmie some tags, separated with commas: ");
    writeTags(new MediaFile(file, tags));
    archiveFile(file);
  }

  private static void archiveFile(Path file) {
    String archivePath = conf.getArchive().toString();
    String filename = file.getFileName().toString();
    System.out.println("Archiving file '" + filename + "' -> " + archivePath);
    try {
      Files.move(file, Paths.get(archivePath, filename));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void writeTags(MediaFile mf) {
    PrintWriter out = null;
    try {
      File tagsFile = conf.getTagsFile().toFile();
      out = new PrintWriter(new BufferedWriter(new FileWriter(tagsFile, true)));
      out.println(format(mf));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null)
        out.close();
    }
  }

  private static String format(MediaFile mf) {
    String tags = mf.getTags().map(Tag::getCode).intersperse(",")
        .foldLeft((a, b) -> a + b, "");
    return String.format("%s:%s", mf.getPath().getFileName().toString(), tags);
  }

  private static void showFile(Path file) throws InterruptedException,
      IOException {
    ProcessBuilder pb = new ProcessBuilder(VIDEO_PLAYER, file.toAbsolutePath()
        .toString());
    pb.start().waitFor();
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

  private static <T> List<T> shuffle(List<T> l) {
    java.util.List<T> c = l.toJavaList();
    Collections.shuffle(c);
    return list(c);
  }

  private static List<Tag> readTags(String message) {
    System.out.println(message);
    String input = new Scanner(System.in).nextLine();
    return list(input.split(",")).map(Tag::tag);
  }

  private static List<Path> findInterestingFiles(Path rootDir)
      throws IOException {
    final ArrayList<Path> files = new ArrayList<>();
    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
        String filename = file.getFileName().toString();
        if (EXTENSIONS.filter(e -> filename.endsWith("." + e)).isNotEmpty())
          files.add(file);
        return FileVisitResult.CONTINUE;
      }
    });
    return List.iterableList(files);
  }
}
