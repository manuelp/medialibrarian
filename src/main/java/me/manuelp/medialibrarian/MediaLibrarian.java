package me.manuelp.medialibrarian;

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

    List<Path> files = findInterestingFiles(conf.getDir());
    System.out.println("Interesting: " + files);
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
