package me.manuelp.medialibrarian;

import fj.data.List;
import fj.data.Set;
import me.manuelp.medialibrarian.data.MediaFile;
import me.manuelp.medialibrarian.data.Tag;

public interface TagsRepository {
  void write(MediaFile mf);

  List<MediaFile> read();

  Set<Tag> listTags();

  boolean alreadyContains(MediaFile mf);

  void update(MediaFile mf);
}
