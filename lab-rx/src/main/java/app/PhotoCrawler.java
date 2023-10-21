package app;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            photoDownloader.getPhotoExamples()
                    .compose(processPhotos())
                    .subscribe(photoSerializer::savePhoto);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) {
        photoDownloader.searchForPhotos(query)
                .compose(processPhotos())
                .subscribe(next -> photoSerializer.savePhoto(next),
                           error -> log.log(Level.SEVERE, "Searching photos error", error));
    }

    public void downloadPhotosForMultipleQueries(List<String> queries) {
        photoDownloader.searchForPhotos(queries)
                .compose(processPhotos())
                .subscribe(next -> photoSerializer.savePhoto(next),
                           error -> log.log(Level.SEVERE, "Searching photos error", error));
    }

    private ObservableTransformer<Photo, Photo> processPhotos()  {
//        return photos -> photos.filter(photoProcessor::isPhotoValid).map(photoProcessor::convertToMiniature);

        //loguje wielkości, żeby móc łatwo sprawdzić czy się dobrze wykonuje
        return photos -> photos
                            .groupBy(PhotoSize::resolve)
                            .flatMap(group -> {
                                PhotoSize size = group.getKey();
                                if(size == PhotoSize.SMALL) {
                                    log.info("Mały");
                                    return Observable.empty();
                                }
                                if(group.getKey() == PhotoSize.MEDIUM) {
                                    log.info("Średni");
                                    return group
                                            .buffer(5, TimeUnit.SECONDS) //buforuje co 5 sekund i z Observable<Photo> robi Observable<List<Photo>>
                                            .flatMapIterable(list -> list); //flatMapujemy List<Photo> (lista jest iterable) na Photo
                                }
                                else {
                                    log.info("Duży");
                                    return group
                                            .observeOn(Schedulers.computation()) //używamy observeOn, żeby od tej pory w dół (w łancuchu) korzystał z odpowiedniego Schedulersa
                                            .map(photoProcessor::convertToMiniature);

                                    // chciałem zrobić jak poniżej, ale nie ma w projekcie zaimportowanego rxandroida,
                                    // który jest potrzebny do korzystania z AndroidSchedulers, dzięki którym można dostać się do głównego wątku
                                    // a trudności z dodaniem go do dependencies przewyższyły moje umiejętności i pokłady cierpliwości
//                                    return group
//                                            .subscribeOn(Schedulers.computation()) // subscribeon powoduje, że działamy na computationach (od początku łańcucha aż do...
//                                            .map(photoProcessor::convertToMiniature)
//                                            .observeOn(AndroidSchedulers.mainThread()); // ... teraz - od tego momentu korzystamy (z powrotem) z głównego wątku
                                }
                            });
    }
}
