package me.manuelp.medialibrarian;

import fj.data.List;
import fj.data.Option;
import jline.console.ConsoleReader;
import me.manuelp.medialibrarian.data.Configuration;
import me.manuelp.medialibrarian.logging.ConsoleLoggerBuilder;
import me.manuelp.medialibrarian.logging.LogLevel;
import me.manuelp.medialibrarian.logging.LoggerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MediaLibrarianTest {
  private Path inbox;
  private Path archive;
  private LoggerBuilder loggerBuilder;

  @Before
  public void setUp() throws IOException {
    loggerBuilder = new ConsoleLoggerBuilder(new ConsoleReader(System.in, System.out), LogLevel.DEBUG);
    Path base = Paths.get("");
    setupInbox(base);
    setupArchive(base);
  }

  private void setupInbox(Path base) throws IOException {
    inbox = Files.createDirectory(base.resolve("test_inbox"));
    Files.createFile(inbox.resolve("f.mp4"));
    Files.createFile(inbox.resolve("f.webm"));
    Files.createFile(inbox.resolve("f.flv"));
    Files.createFile(inbox.resolve("something-else.txt"));
    Path legacy = Files.createDirectory(inbox.resolve("legacy"));
    Files.createFile(legacy.resolve("f.avi"));
    Files.createFile(legacy.resolve("f.mpg"));
    Files.createFile(legacy.resolve("f.wmv"));
  }

  private void setupArchive(Path base) throws IOException {
    archive = Files.createDirectory(base.resolve("test_archive"));
  }

  @After
  public void tearDown() throws IOException {
    recursivelyDelete(inbox);
    recursivelyDelete(archive);
  }

  private void recursivelyDelete(Path p) throws IOException {
    Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
        Files.delete(path);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
        Files.delete(path);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  @Test
  public void should_walk_inbox_tree_to_find_files_with_known_extension() throws IOException {
    TagsRepository repo = new VolatileTagsRepository();
    Configuration conf = new Configuration("vlc", inbox, archive, Option.none());
    MediaLibrarian mediaLibrarian = new MediaLibrarian(conf, repo, loggerBuilder);

    List<Path> files = mediaLibrarian.findFiles(inbox);

    assertTrue("No media file found.", files.isNotEmpty());
    assertEquals("Wrong number of media files found.", 6, files.length());
  }
}