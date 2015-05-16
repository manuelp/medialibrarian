package me.manuelp.medialibrarian;

import fj.data.List;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SimpleFileTagsRepository implements TagsRepository {
  private File tagsFile;

  public SimpleFileTagsRepository(File tagsFile) {
    this.tagsFile = tagsFile;
  }

  @Override
  public void write(MediaFile mf) {
    PrintWriter out = null;
    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter(tagsFile, true)));
      out.println(format(mf));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null)
        out.close();
    }
  }

  @Override
  public List<MediaFile> read() {
    ArrayList<MediaFile> files = new ArrayList<>();
    try {
      Files.lines(tagsFile.toPath()).map(l -> l.split(":")).map(t -> {
        Path file = Paths.get(t[0]);
        List<Tag> tags = List.list(t[1].split(",")).map(Tag::new);
        return new MediaFile(file, tags);
      }).forEach(mf -> files.add(mf));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return List.iterableList(files);
  }

  private String format(MediaFile mf) {
    String tags = mf.getTags().map(Tag::getCode).intersperse(",")
        .foldLeft((a, b) -> a + b, "");
    return String.format("%s\t%s", mf.getPath().getFileName().toString(), tags);
  }
}
