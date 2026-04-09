public class Song
{
    // instance variables - replace the example below with your own
    private String bandName;
    private String songName;
    private String albumCover;
    private String songFile;

    public Song(String bandName, String songName, String albumCover, String songFile)
    {
        // initialise instance variables
        this.bandName = bandName;
        this.songName = songName;
        this.albumCover = albumCover;
        this.songFile = songFile;
    }
    
    public String returnBand(){
        return bandName;
    }
    
    public String returnSong(){
        return songName;
    }
    
    public String returnCover(){
        return albumCover;
    }
    
    public String returnFile(){
        return songFile;
    }
}