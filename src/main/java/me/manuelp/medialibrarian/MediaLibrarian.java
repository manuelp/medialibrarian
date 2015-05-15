package me.manuelp.medialibrarian;

import fj.F;
import fj.data.List;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class MediaLibrarian {
  private final static List<String> EXTENSIONS = List.list("mp4", "flv", "avi",
    "mpg", "webm", "wmv");

  public static void main(String[] args) throws IOException {
    System.out.println("----[ MediaLibrarian welcomes you ]----");
    Path source = Paths.get("test", "input");
    Path archive = Paths.get("test", "archive");
    Configuration conf = new Configuration(source, archive);
    System.out.println("Configuration: " + conf.toString());

    final ArrayList<Path> files = new ArrayList<>();
    Files.walkFileTree(conf.getDir(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
        files.add(file);
        return FileVisitResult.CONTINUE;
      }
    });
    List<Path> interesting = List.iterableList(files).filter(isInteresting());

    System.out.println("Interesting: " + interesting);
  }

  private static F<Path, Boolean> isInteresting() {
    return p -> {
      String filename = p.getFileName().toString();
      return EXTENSIONS.filter(e -> filename.endsWith("." + e)).isNotEmpty();
    };
  }
}
