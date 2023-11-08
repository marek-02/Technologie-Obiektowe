package controller;


import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import model.Gallery;
import model.Photo;
import util.PhotoDownloader;

public class GalleryController {

    private Gallery galleryModel;

    @FXML
    private TextField imageNameField;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<Photo> imagesListView;

    @FXML
    private TextField searchTextField;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button searchButton;

    @FXML
    public void initialize() {
        imagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Photo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView photoIcon = new ImageView(item.getPhotoData());
                    photoIcon.setPreserveRatio(true);
                    photoIcon.setFitHeight(50);
                    setGraphic(photoIcon);
                }
            }
        });

        imagesListView.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if(oldValue != null) {
                        imageNameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                    }
                    if(newValue != null) { //null jest gdy mieliśmy wybrane jakieś zdjęcie i zaczęliśmy wyszukiwać nowych czyli zrobiło clear w galerry
                        bindSelectedPhoto(newValue);
                    }
                }));

        progressIndicator.setVisible(false);
    }

    public void setModel(Gallery gallery) {
        this.galleryModel = gallery;


        Platform.runLater(() -> {
            imagesListView.setItems(gallery.getPhotos());
            imagesListView.getSelectionModel().select(0);
        });

//        bindSelectedPhoto(gallery.getPhotos().get(0));
    }

    private void bindSelectedPhoto(Photo selectedPhoto) {
        imageNameField.textProperty().bindBidirectional(selectedPhoto.nameProperty());
        imageView.imageProperty().bind(selectedPhoto.imageProperty());
    }
    public void searchButtonClicked(ActionEvent event) {
        progressIndicator.setVisible(true);
        searchButton.setDisable(true);

        PhotoDownloader photoDownloader = new PhotoDownloader();
        galleryModel.clear();

        photoDownloader.searchForPhotos(searchTextField.getText()).subscribeOn(Schedulers.computation()) //gdy subscribeOn jest w PhotoDownloaderze to nie działa - czemu?
                .subscribe(
                        galleryModel::addPhoto,
                        Throwable::printStackTrace,
                        () -> {
                            progressIndicator.setVisible(false);
                            searchButton.setDisable(false);
                        }
                );
    }
}

