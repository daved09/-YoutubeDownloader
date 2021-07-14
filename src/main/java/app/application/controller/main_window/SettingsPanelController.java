package app.application.controller.main_window;

import app.application.data.VersionProperties;
import app.application.utils.UserConfigHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/views/MainWindow.fxml")
public class SettingsPanelController {

	@FXML
	private TextField txtDownloadPath;

	@FXML
	private TextField txtVerion;

	@Autowired
	private UserConfigHandler userConfigHandler;

	@Autowired
	private VersionProperties versionProperties;

	@FXML
	public void initialize(){
		txtDownloadPath.textProperty().bindBidirectional(userConfigHandler.getUserConfig().getDownloadDir());
		txtVerion.textProperty().bind(versionProperties.getVersion());
	}

	public void btnSave_click(){
		userConfigHandler.writeConfig();
	}

}
