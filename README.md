# medialibrarian

A CLI client to classify and archive media files.

## Changelog
### 0.2
* Upgrade to Java 10 and FJ 4.8.

### 0.1.1 (2017-04-01)
Upgraded dependencies.

### 0.1
#### Fix
* Fixed view by tags

#### Changes
* Added simple functional logging framework.
* Upgrade to FunctionalJava 4.6

### 0.0.4

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

## Compile

Just use Maven: `mvn package`.