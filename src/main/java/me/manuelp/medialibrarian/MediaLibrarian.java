package me.manuelp.medialibrarian;

import fj.data.List;
import me.manuelp.medialibrarian.data.Configuration;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;

import static fj.data.List.list;

public class MediaLibrarian {
  private final static List<String> EXTENSIONS = list("mp4", "flv", "avi",
    "mpg", "webm", "wmv");
  private static final String VIDEO_PLAYER = "vlc";
  private static Configuration conf;
  private TagsRepository tagsRepository;

  public MediaLibrarian(Configuration conf, TagsRepository tagsRepository) {
    this.conf = conf;
    this.tagsRepository = tagsRepository;
  }

  public List<Path> findFiles(Path rootDir) throws IOException {
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
    Collections.shuffle(files);
    return List.iterableList(files);
  }

  public void showFile(Path file) throws InterruptedException, IOException {
    ProcessBuilder pb = new ProcessBuilder(VIDEO_PLAYER, file.toAbsolutePath()
        .toString());
    pb.start().waitFor();
  }

  public void archive(Path file, List<Tag> tags) {
    tagsRepository.write(new MediaFile(file, tags));
    archiveFile(file);
  }

  private static void archiveFile(Path file) {
    String archivePath = conf.getArchive().toString();
    String filename = file.getFileName().toString();
    try {
      Files.move(file, Paths.get(archivePath, filename));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
