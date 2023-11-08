package model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Gallery {

    private final ObservableList<Photo> photos = FXCollections.observableArrayList();

    public void addPhoto(Photo photo) {
        Platform.runLater(() -> photos.add(photo));
    }

    public ObservableList<Photo> getPhotos() {
        return photos;
    }

    public void clear() {
        photos.clear();
    }
}
