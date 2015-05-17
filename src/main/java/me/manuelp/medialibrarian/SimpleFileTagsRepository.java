package me.manuelp.medialibrarian;

import fj.Ord;
import fj.data.List;
import fj.data.Set;
import me.manuelp.medialibrarian.data.Hash;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static me.manuelp.medialibrarian.data.Hash.hash;

public class SimpleFileTagsRepository implements TagsRepository {
  private File tagsFile;

  public SimpleFileTagsRepository(File tagsFile) {
    this.tagsFile = tagsFile;
    if(!tagsFile.exists()) {
      try {
        tagsFile.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void write(MediaFile mf) {
    if (alreadyContains(mf))
      throw new RuntimeException("Tags repository already contains this file: "
          + mf.getFilename());

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
  public boolean alreadyContains(MediaFile mf) {
    return read().filter(f -> f.sameHash(mf)).isNotEmpty();
  }

  private String format(MediaFile mf) {
    String tags = mf.getTags().toList().map(Tag::getCode).intersperse(",")
        .foldLeft((a, b) -> a + b, "");
    return String.format("%s:%s:%s", mf.getHash().getString(), mf.getPath()
        .getFileName().toString(), tags);
  }

  @Override
  public List<MediaFile> read() {
    ArrayList<MediaFile> files = new ArrayList<>();
    try {
      Files
          .lines(tagsFile.toPath())
          .map(l -> l.split(":"))
          .map(
            t -> {
              Hash hash = hash(t[0]);
              Path file = Paths.get(t[1]);
              Set<Tag> tags = Set.set(Ord.stringOrd, t[2].split(",")).map(
                Ord.hashOrd(), Tag::tag);
              return new MediaFile(hash, file, tags);
            }).forEach(mf -> files.add(mf));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return List.iterableList(files);
  }

  @Override
  public Set<Tag> listTags() {
    List<Set<Tag>> x = read().map(mediaFile -> mediaFile.getTags());
    Set<Tag> tags = x.foldLeft1(Set::union);
    return tags;
  }

  @Override
  public void update(MediaFile mf) {
    if (!alreadyContains(mf))
      throw new RuntimeException("Unknown media file: " + mf.getFilename());
    List<MediaFile> all = read().map(f -> f.sameHash(mf) ? f.mergeTags(mf) : f);
    emptyFile(this.tagsFile);
    writeAll(all);
  }

  private void emptyFile(File f) {
    try {
      new RandomAccessFile(f, "rw").setLength(0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeAll(List<MediaFile> files) {
    files.foreachDoEffect(f -> write(f));
  }
}
