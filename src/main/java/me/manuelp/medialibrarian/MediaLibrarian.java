package me.manuelp.medialibrarian;

import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import fj.function.Effect2;
import me.manuelp.medialibrarian.data.Configuration;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;
import me.manuelp.medialibrarian.logging.LogLevel;
import me.manuelp.medialibrarian.logging.LoggerBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static fj.data.List.list;
import static me.manuelp.medialibrarian.logging.LogLevel.DEBUG;
import static me.manuelp.medialibrarian.logging.LogLevel.INFO;

public class MediaLibrarian {
  private final static List<String> EXTENSIONS   = list("mp4", "flv", "avi",
                                                        "mpg", "webm", "wmv");
  private static final String       VIDEO_PLAYER = "vlc";
  private       Configuration             conf;
  private       TagsRepository            tagsRepository;
  private final Effect2<String, LogLevel> log;

  public MediaLibrarian(Configuration conf, TagsRepository tagsRepository,
                        LoggerBuilder loggerBuilder) {
    this.conf = conf;
    this.tagsRepository = tagsRepository;
    this.log = loggerBuilder.logger(this);
  }

  public List<Path> findFiles(Path rootDir) throws IOException {
    log.f(String.format(
        "Searching for files with extensions %s from directory %s...",
        EXTENSIONS.toString(), rootDir.toAbsolutePath().toString()), DEBUG);
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

  public void showFile(Path file) {
    ProcessBuilder pb = new ProcessBuilder(VIDEO_PLAYER,
                                           file.toAbsolutePath().toString());
    try {
      pb.start().waitFor();
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void showFiles(List<Path> files) {
    List<String> tokens = files.map(Path::toAbsolutePath).map(Path::toString)
                               .cons(VIDEO_PLAYER);
    ProcessBuilder pb = new ProcessBuilder(tokens.toJavaList());
    try {
      pb.start().waitFor();
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void archive(Path file, Set<Tag> tags) {
    MediaFile mf = new MediaFile(file, tags);
    if (tagsRepository.alreadyContains(mf)) {
      log.f("File already present, merging tags.", INFO);
      tagsRepository.update(mf);
      try {
        Files.delete(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.f("Deleted " + file.toString(), DEBUG);
    } else {
      tagsRepository.write(mf);
      archiveFile(file);
    }
  }

  private void archiveFile(Path file) {
    String archivePath = conf.getArchive().toString();
    String filename    = file.getFileName().toString();
    try {
      log.f(String.format("Moving %s -> %s", file, archivePath), DEBUG);
      Files.move(file, Paths.get(archivePath, filename));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Path> findByTags(Option<Set<Tag>> tags) {
    return tagsRepository.read().filter(
        mf -> tags.isNone() || tags.some().subsetOf(mf.getTags())).map(
        MediaFile::getPath).map(p -> Paths
        .get(conf.getArchive().toString(), p.getFileName().toString()));
  }
}
