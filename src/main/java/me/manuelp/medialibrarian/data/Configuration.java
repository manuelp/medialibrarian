package me.manuelp.medialibrarian.data;

import fj.data.Option;
import fj.data.Set;
import me.manuelp.medialibrarian.validations.Checks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static fj.P.p;

public class Configuration {
    private final String videoPlayer;
    private final Path dir, archive;
    private Option<Set<Tag>> tagsToView;

    public Configuration(String videoPlayer, Path dir, Path archive, Option<Set<Tag>> tagsToView) {
        Checks.notNull(p("Video player", videoPlayer), p("Source path", dir), p("Archive path", archive),
                p("Tags to view", tagsToView));
        this.videoPlayer = videoPlayer;
        this.dir = dir;
        this.archive = archive;
        this.tagsToView = tagsToView;
    }

    public String getVideoPlayer() {
        return videoPlayer;
    }

    public Path getDir() {
        return dir;
    }

    public Path getArchive() {
        return archive;
    }

    public Path getTagsFile() {
        return Paths.get(archive.toString(), "tags.tgs");
    }

    public boolean viewMode() {
        return tagsToView.isSome();
    }

    public Option<Set<Tag>> getTagsToView() {
        return tagsToView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(videoPlayer, that.videoPlayer) &&
                Objects.equals(dir, that.dir) &&
                Objects.equals(archive, that.archive) &&
                Objects.equals(tagsToView, that.tagsToView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoPlayer, dir, archive, tagsToView);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "videoPlayer='" + videoPlayer + '\'' +
                ", dir=" + dir +
                ", archive=" + archive +
                ", tagsToView=" + tagsToView +
                '}';
    }
}
