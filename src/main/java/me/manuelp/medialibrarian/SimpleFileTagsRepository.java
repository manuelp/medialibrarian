package me.manuelp.medialibrarian;

import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

import java.io.*;

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

  private String format(MediaFile mf) {
    String tags = mf.getTags().map(Tag::getCode).intersperse(",")
        .foldLeft((a, b) -> a + b, "");
    return String.format("%s:%s", mf.getPath().getFileName().toString(), tags);
  }
}
