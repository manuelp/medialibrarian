package me.manuelp.medialibrarian;

import fj.data.List;
import fj.data.Set;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

public class VolatileTagsRepository implements TagsRepository {

  private List<MediaFile> files;

  public VolatileTagsRepository() {
    files = List.list();
  }

  @Override
  public void write(MediaFile mf) {
    files = files.cons(mf);
  }

  @Override
  public List<MediaFile> read() {
    return files;
  }

  @Override
  public Set<Tag> listTags() {
    return Set.empty(Tag.ord());
  }

  @Override
  public boolean alreadyContains(MediaFile mf) {
    return files.exists(f -> f.equals(mf));
  }

  @Override
  public void update(MediaFile mf) {
    if(!alreadyContains(mf))
      throw new RuntimeException("Tags repository already contains this file: "
                                 + mf.getFilename());
    files = files.map(f -> f.sameHash(mf) ? f.mergeTags(mf) : f);
  }
}
