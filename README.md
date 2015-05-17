# medialibrarian

A CLI client to classify and archive media files.

## Changelog

### 0.0.4-SNAPSHOT

* Fixed fields separator in tags file (:).
* Added hash computing for every archived file.
* Deduplication of archived files: the original archived copy of the file is maintained, tags are merged, and the
  new file is deleted instead of moved.

### 0.0.3

* Added view option, can specify a set of tags to filter the archived files.
* Prints the used tags when viewing.

### 0.0.2

* Added filename and progress printing while viewing videos.
* Used jline2 to print messages, show prompts, read stuff with some more sophisticated editing capabilities.

### 0.0.1

First release with basic functionality for viewing, tagging and archiving video files.