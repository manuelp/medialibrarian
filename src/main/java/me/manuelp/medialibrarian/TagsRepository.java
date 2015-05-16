package me.manuelp.medialibrarian;

import fj.data.List;
import me.manuelp.medialibrarian.data.MediaFile;

public interface TagsRepository {
  void write(MediaFile mf);

  List<MediaFile> read();
}
