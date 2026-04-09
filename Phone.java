import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import java.io.FileInputStream;
import javafx.util.Duration;
import java.io.Reader;
import java.util.Map;
import java.io.File;

public class Phone extends Application
{
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songNames;
    private double durationInSeconds;
    private ProgressBar progressBar;
    private ImageView albumDisplay;
    private Button forwardButton;
    private Button backButton;
    private Timeline timeline;
    private boolean isPlaying;
    private Image albumCover;
    private String musicFile;
    private Button myButton;
    private ImageView play;
    private Label songName;
    private Label bandName;
    private String image;
    private int sec = 0;
    private int min = 0;
    private Label prog;
    private int endMin;
    private int endSec;
    private Label end;
    private int i = 0;
    
    @Override
    public void start(Stage stage) throws InterruptedException
    {
        isPlaying = true;
        
        songNames = new ArrayList<Song>();
        addAllSongs();
        
        albumCover = new Image(songNames.get(i).returnCover());
        albumDisplay = new ImageView(albumCover);
        albumDisplay.setFitWidth(360);
        albumDisplay.setFitHeight(360);
        
        musicFile = songNames.get(i).returnFile();   
        
        Media song = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(song);
        
        mediaPlayer.setOnReady(() -> {
            Duration duration = song.getDuration();
            // Convert duration from milliseconds to hours, minutes, and seconds
            long totalSeconds = (long) duration.toSeconds();
            endMin = (int)(totalSeconds % 3600) / 60;
            endSec = (int)(totalSeconds % 60);
             
            end.setText(endMin+":"+String.format("%02d", endSec));
        });
        
        mediaPlayer.play();
        
        songName = new Label(songNames.get(i).returnSong());
        songName.setFont(new Font(16));
        bandName = new Label(songNames.get(i).returnBand());
        bandName.setFont(new Font(15));
        
        VBox vb = new VBox();
        vb.setPadding(new Insets(0, 10, 0, 10)); 
        
        vb.getChildren().addAll(songName, bandName);
        
        BorderPane p = new BorderPane();
        
        prog = new Label(min+":"+ String.format("%02d", sec));
        p.setMargin(prog,new Insets(5,0,0,0));
        
        end =  new Label(endMin+":"+endSec);
        p.setMargin(end,new Insets(5,0,0,0));
        
        progressBar = new ProgressBar(); // Starts at 50%
        progressBar.setMinHeight(10);
        progressBar.setMaxHeight(10);
        progressBar.setMaxWidth(Double.MAX_VALUE*0.60);
        progressBar.setProgress(0);
        
        p.setTop(progressBar);
        p.setLeft(prog);
        p.setRight(end);
        
        forwardButton = new Button();
        forwardButton.setShape(new Circle(30));
        forwardButton.setMinSize(50,50);
        forwardButton.setMaxSize(50,50);
        
        // Create a Button or any control item
        myButton = new Button();
        myButton.setShape(new Circle(30));
        myButton.setMinSize(50,50);
        myButton.setMaxSize(50,50);
        
        backButton = new Button();
        backButton.setShape(new Circle(30));
        backButton.setMinSize(50,50);
        backButton.setMaxSize(50,50);
        
        ImageView forward = new ImageView();
        forward.setImage(new Image("/assets/forward.png"));
        forward.setFitWidth(25);
        forward.setFitHeight(25);
        forwardButton.setGraphic(forward);
        
        play = new ImageView();
        play.setImage(new Image("/assets/pauseButton.png"));
        play.setFitWidth(25);
        play.setFitHeight(25);
        myButton.setGraphic(play);
        
        ImageView back = new ImageView();
        back.setImage(new Image("/assets/back.png"));
        back.setFitWidth(25);
        back.setFitHeight(25);
        backButton.setGraphic(back);

        // Create a new grid pane
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setMinSize(300, 300);

        //set an action on the button using method reference
        forwardButton.setOnAction(this::forwardSong);
        myButton.setOnAction(this::buttonClick);
        backButton.setOnAction(this::backSong);
        
        HBox hbox = new HBox();
        
        hbox.getChildren().addAll(forwardButton, myButton, backButton);
        hbox.setMargin(forwardButton, new Insets(5,5,5,5));
        hbox.setMargin(myButton, new Insets(5,5,5,5));
        hbox.setMargin(backButton, new Insets(5,5,5,5));
        hbox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setMargin(albumDisplay, new Insets(10));
        vbox.setMargin(p, new Insets(5,10,10,10));
        vbox.getChildren().addAll(albumDisplay, vb, p, hbox);
        
        // Add the button and label into the pane
        pane.setCenter(vbox);

        // JavaFX must have a Scene (window content) inside a Stage (window)
        Scene scene = new Scene(pane, 400,575);
        stage.setTitle("Music Player");
        stage.setScene(scene);
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            sec++;
            if (min == endMin && sec == endSec){
                i++;
                loadNext(i);
            }
            if (sec == 60) {
                sec = 0;
                min++;
            }
            prog.setText(min + ":" + String.format("%02d", sec));
            progressBar.setProgress((((min*60)+sec)/ (double)((endMin*60)+endSec)));
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Show the Stage (window)
        stage.show();
    }
    
    public void buttonClick(ActionEvent event)
    {   
        if (isPlaying){
            mediaPlayer.play();
            timeline.play();
            play.setImage(new Image("/assets/pauseButton.png"));
        } else{
            mediaPlayer.pause();
            timeline.pause();
            play.setImage(new Image("/assets/playButton.png"));
        }
        
        isPlaying = !isPlaying;
    }
    
    public void forwardSong(ActionEvent event){
        if (i >= songNames.size()){
            i = 0;
        } else {
            i++;
        }
        
        loadNext(i);
    }
    
    public void backSong(ActionEvent event){
        if (i <= 0){
            i = songNames.size() - 1;
        } else{
            i--;
        }
        
        loadNext(i);
    }
    
    public void loadNext(int index){
        mediaPlayer.stop();
        timeline.stop();
        
        songName.setText(songNames.get(index).returnSong());
        bandName.setText(songNames.get(index).returnBand());
        
        albumCover = new Image(songNames.get(index).returnCover());
        albumDisplay.setImage(albumCover);
        
        musicFile = songNames.get(index).returnFile();
        songNames.get(index).returnCover();
        
        Media nextSong = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(nextSong);
        
        mediaPlayer.setOnReady(() -> {
            Duration duration = nextSong.getDuration();
            // Convert duration from milliseconds to hours, minutes, and seconds
            long totalSeconds = (long) duration.toSeconds();
            endMin = (int)(totalSeconds % 3600) / 60;
            endSec = (int)(totalSeconds % 60);
             
            end.setText(endMin+":"+String.format("%02d", endSec));
        });
        
        min = 0;
        sec = 0;
        progressBar.setProgress(0);
        
        mediaPlayer.play();
        timeline.play();
    }
    
    public void addAllSongs(){
        addSong(new Song("Ghost", "Peacefield", "/albums/ghost.png", "songs/peacefield.mp3"));
        addSong(new Song("Escape the Fate", "10 Miles Wide", "/albums/escapeFate.png", "songs/10MilesWide.mp3"));
        addSong(new Song("Avenged Sevenfold", "A Little Piece of Heaven", "/albums/avengedSevenfold.png", "songs/littlePieceOfHeaven.mp3"));
        addSong(new Song("Marilyn Manson", "The Beautiful People", "/albums/marilynManson.png", "songs/beautifulPeople.mp3"));
        addSong(new Song("Slipknot", "The Devil in I", "/albums/slipknot.png", "songs/devilInI.mp3"));
    }
    
    public void addSong(Song song){
        songNames.add(song);
    }
}