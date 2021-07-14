package app.application.controller.main_window;

import app.application.data.VersionProperties;
import app.application.utils.UserConfigHandler;
import app.application.utils.YoutubeDownloadListener;
import app.application.utils.YoutubeIdExtractor;
import app.application.utils.YoutubeVideoDownloadService;
import com.github.kiulian.downloader.model.VideoDetails;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("/views/VideoPanel.fxml")
public class VideoPanelController {

	@FXML
	private TextField txtDownloadLink;

	@FXML
	private ImageView imgThumbnail;

	@FXML
	private AnchorPane videoPane;

	@FXML
	private Label lblVideoTitle;

	@FXML
	private ComboBox<String> boxQuality;

	@FXML
	private TextArea txtDescription;

	@FXML
	private ProgressBar downloadProgress;

	@FXML
	private CheckBox chkAudioOnly;

	@FXML
	private Button btnSearch;

	@Autowired
	private YoutubeVideoDownloadService youtubeVideoDownloadService;

	@Autowired
	private YoutubeIdExtractor youtubeIdExtractor;

	@FXML
	private void initialize(){
		youtubeVideoDownloadService.setYoutubeDownloadListener(new YoutubeDownloadListener(downloadProgress));
		btnSearch.disableProperty().bind(Bindings.isEmpty(txtDownloadLink.textProperty()));
	}


	public void btnSearch_click(){
		VideoDetails details = youtubeVideoDownloadService.getVideoDetails(youtubeIdExtractor.getVideoIdFromLink(txtDownloadLink.getText()));
		imgThumbnail.setImage(new Image(details.thumbnails().get(0).split("\\?sqp")[0]));
		lblVideoTitle.setText(details.title());
		refreshQualityBox(youtubeVideoDownloadService.getQualityLabels());
		txtDescription.setText(details.description());
		videoPane.setVisible(true);
	}

	public void btnDownloadVideo_click(){
		if(chkAudioOnly.isSelected()){
			youtubeVideoDownloadService.downloadAudioOnlyAsync();
		}
		else{
			youtubeVideoDownloadService.downloadVideoAsync(boxQuality.getSelectionModel().getSelectedItem());
		}
	}



	private void refreshQualityBox(List<String> listWithOptions){
		boxQuality.getItems().clear();
		boxQuality.getItems().addAll(listWithOptions);
		boxQuality.getSelectionModel().select(0);
	}


}
