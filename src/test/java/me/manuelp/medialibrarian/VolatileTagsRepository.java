package me.manuelp.medialibrarian;

import fj.data.List;
import fj.data.Set;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

public class VolatileTagsRepository implements TagsRepository {
  @Override
  public void write(MediaFile mf) {

  }

  @Override
  public List<MediaFile> read() {
    return List.list();
  }

  @Override
  public Set<Tag> listTags() {
    return Set.empty(Tag.ord());
  }

  @Override
  public boolean alreadyContains(MediaFile mf) {
    return false;
  }

  @Override
  public void update(MediaFile mf) {

  }
}
