package app.application.utils;

import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistVideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubePlaylistDownloadService extends YoutubeDownloadService {

    private PlaylistInfo playlistInfo;

    @Setter
    private Label label;

    @SneakyThrows
    public PlaylistInfo getPlaylistInfo(String playListId) {
        RequestPlaylistInfo requestPlaylistInfo = new RequestPlaylistInfo(playListId);
        playlistInfo = youtubeDownloader.getPlaylistInfo(requestPlaylistInfo).data();
        return playlistInfo;
    }


    public List<String> getVideoTitles(){
        List<String> titles = new ArrayList<>();
        for (PlaylistVideoDetails video : playlistInfo.videos()) {
            titles.add(video.title());
        }
        setLabelProgress(0, titles.size());
        return titles;
    }

    @SneakyThrows
    public void downloadPlaylist(){
        int size = playlistInfo.videos().size();
        int progress = 0;
        for (PlaylistVideoDetails video : playlistInfo.videos()) {
            progress++;
            setLabelProgress(progress, size);
            downloadAsync(getVideoInfo(video.videoId()));
        }
    }


    private VideoInfo getVideoInfo(String videoID){
        RequestVideoInfo requestVideoInfo = new RequestVideoInfo(videoID);
        return youtubeDownloader.getVideoInfo(requestVideoInfo).data();
    }

    protected void downloadAsync(VideoInfo videoInfo){
        RequestVideoFileDownload requestVideoFileDownload = new RequestVideoFileDownload(videoInfo.bestVideoWithAudioFormat());
        requestVideoFileDownload.renameTo(videoInfo.details().title()).async().overwriteIfExists(true)
                .saveTo(Paths.get(userConfigHandler.getUserConfig().getDownloadDir().get() + File.separator + playlistInfo.details().title()).toFile());
        youtubeDownloader.downloadVideoFile(requestVideoFileDownload);
    }

    private void setLabelProgress(int current, int max){
        Platform.runLater(() -> label.setText("Videos: " + current + "/" + max));
    }

}
