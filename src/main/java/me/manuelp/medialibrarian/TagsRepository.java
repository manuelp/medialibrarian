package me.manuelp.medialibrarian;

import me.manuelp.medialibrarian.data.MediaFile;

public interface TagsRepository {
  void write(MediaFile mf);
}
