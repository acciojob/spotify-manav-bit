package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    // Maps to hold the relationships between entities
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    // Lists to hold individual entities
    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    // Like flag to prevent multiple likes by a single user on a single song
    private boolean like=false;

    // Constructor to initialize the repository
    public SpotifyRepository(){
        // Initializing all the hashmaps and lists
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        // Check if user already exists, if yes return the existing user
        for(User user:users){
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        // If user doesn't exist, create a new user and add to the list
        User newUser=new User(name,mobile);
        users.add(newUser);
        return newUser;
    }

    public Artist createArtist(String name) {
        // Check if artist already exists, if yes return the existing artist
        for(Artist artist:artists){
            if(artist.getName().equals(name)){
                return artist;
            }
        }
        // If artist doesn't exist, create a new artist and add to the list
        Artist newArtist=new Artist(name);
        artists.add(newArtist);
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        // Get the artist object if it exists
        Artist artist=null;
        for(Artist existingArtist:artists){
            if(existingArtist.getName().equals(artistName)){
                artist=existingArtist;
                break;
            }
        }
        // Create a new artist if it doesn't exist
        if(artist==null){
            artist=createArtist(artistName);
        }
        // Create a new album
        Album newAlbum=new Album(title);
        // Add the album to the list if it's not already present
        if(!albums.contains(newAlbum)){
            albums.add(newAlbum);
        }
        // Update the artist-album map
        if(artistAlbumMap.containsKey(artist)){
            List<Album> allAlbums=artistAlbumMap.get(artist);
            boolean albumExists=false;
            for(Album existingAlbum:allAlbums){
                if(existingAlbum.getTitle().equals(title)){
                    albumExists=true;
                    break;
                }
            }
            if(!albumExists){
                allAlbums.add(newAlbum);
            }
        }
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        // Get the album object if it exists
        Album album=null;
        for(Album existingAlbum:albums){
            if(existingAlbum.getTitle().equals(albumName)){
                album=existingAlbum;
                break;
            }
        }
        // Throw exception if album doesn't exist
        if(album==null){
            throw new Exception("Album does not exist");
        }

        // Create a new song
        Song newSong = new Song(title, length);

        // Get the list of songs for the album
        List<Song> songList = albumSongMap.get(album);

        // Create the list if it doesn't exist
        if(songList == null){
            songList = new ArrayList<>();
            albumSongMap.put(album, songList);
        }

        // Add the new song to the list
        songList.add(newSong);

        // Add the song to the list of songs
        if(!songs.contains(newSong)){
            songs.add(newSong);
        }

        return newSong;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        // Create a new playlist
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        // Get the user object if it exists
        User user=null;
        for(User existingUser:users){
            if(existingUser.getMobile().equals(mobile)){
                user=existingUser;
                break;
            }
        }

        // Throw exception if user doesn't exist
        if(user==null){
            throw new Exception("User does not exist");
        }

        // Create a new list for playlist songs
        List<Song> playlistSongs=new ArrayList<>();

        // Add songs to the playlist if their length matches the specified length
        for(Song song:songs){
            if(song.getLength()==length){
                playlistSongs.add(song);
            }
        }

        // Map the playlist songs
        playlistSongMap.put(playlist,playlistSongs);

        // Map the creator with the playlist
        creatorPlaylistMap.put(user,playlist);

        // Add the user as a listener
        List<User> listenerList=new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);

        // Map the user with the created playlist
        List<Playlist> userPlaylists=new ArrayList<>();
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        // Create a new playlist
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        // Get the user object if it exists
        User user=null;
        for(User existingUser:users){
            if(existingUser.getMobile().equals(mobile)){
                user=existingUser;
                break;
            }
        }

        // Throw exception if user doesn't exist
        if(user==null){
            throw new Exception("User does not exist");
        }

        // Create a new list for playlist songs
        List<Song> playlistSongs=new ArrayList<>();

        // Add songs to the playlist if their title matches any of the specified song titles
        for(Song song:songs){
            if(songTitles.contains(song.getTitle())){
                playlistSongs.add(song);
            }
        }

        // Map the playlist songs
        playlistSongMap.put(playlist,playlistSongs);

        // Map the creator with the playlist
        creatorPlaylistMap.put(user,playlist);

        // Add the user as a listener
        List<User> listenerList=new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);

        // Map the user with the created playlist
        List<Playlist> userPlaylists=new ArrayList<>();
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        // Get the user object if it exists
        User user=null;
        for(User existingUser:users){
            if(existingUser.getMobile().equals(mobile)){
                user=existingUser;
                break;
            }
        }

        // Throw exception if user doesn't exist
        if(user==null){
            throw new Exception("User does not exist");
        }

        // Get the playlist object if it exists
        Playlist playlist=null;
        for(Playlist existingPlaylist:playlists){
            if(existingPlaylist.getTitle().equals(playlistTitle)){
                playlist=existingPlaylist;
                break;
            }
        }

        // Throw exception if playlist doesn't exist
        if(playlist==null){
            throw new Exception("Playlist does not exist");
        }

        // Add the user as a listener if they are not the creator or already a listener
        List<User> listeners = playlistListenerMap.get(playlist);
        if(!listeners.contains(user)){
            listeners.add(user);
        }

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        // Check if the like flag is set
        if(!like){
            // Set the like flag to prevent multiple likes
            like = true;

            // Get the user object if it exists
            User user=null;
            for(User existingUser:users){
                if(existingUser.getMobile().equals(mobile)){
                    user=existingUser;
                    break;
                }
            }

            // Throw exception if user doesn't exist
            if(user == null){
                throw new Exception("User does not exist");
            }

            // Get the song object if it exists
            Song song=null;
            for(Song existingSong:songs){
                if(existingSong.getTitle().equals(songTitle)){
                    song=existingSong;
                    break;
                }
            }

            // Throw exception if song doesn't exist
            if(song == null){
                throw new Exception("Song does not exist");
            }

            // Get the list of users who liked the song, or create a new list if it doesn't exist
            List<User> likedUsers = songLikeMap.get(song);
            if(likedUsers == null){
                likedUsers = new ArrayList<>();
                songLikeMap.put(song, likedUsers);
            }

            // Add the user to the list if they haven't already liked the song
            if(!likedUsers.contains(user)){
                likedUsers.add(user);
                // Increment the song's likes
                int likes = song.getLikes();
                song.setLikes(likes+1);

                // Find the album for the song
                for(Album album:albums){
                    List<Song> songsInAlbum = albumSongMap.get(album);
                    if(songsInAlbum.contains(song)){
                        // Find the artist for the album
                        for(Artist artist:artists){
                            List<Album> albumsByArtist = artistAlbumMap.get(artist);
                            if(albumsByArtist.contains(album)){
                                // Increment the artist's likes
                                int artistLikes = artist.getLikes();
                                artist.setLikes(artistLikes+1);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            return song;
        }
        return null;
    }

    public String mostPopularArtist() {
        // If there are no artists, return a message
        if(artists.isEmpty()){
            return "No artists found";
        }

        // Find the artist with the most likes
        int maxLikes = Integer.MIN_VALUE;
        Artist mostPopularArtist = null;
        for(Artist artist:artists){
            if(artist.getLikes() > maxLikes){
                maxLikes = artist.getLikes();
                mostPopularArtist = artist;
            }
        }

        // Return the name of the most popular artist
        return mostPopularArtist.getName();
    }

    public String mostPopularSong() {
        // If there are no songs, return a message
        if(songs.isEmpty()){
            return "No songs found";
        }

        // Find the song with the most likes
        int maxLikes = Integer.MIN_VALUE;
        Song mostPopularSong = null;
        for(Song song:songs){
            if(song.getLikes() > maxLikes){
                maxLikes = song.getLikes();
                mostPopularSong = song;
            }
        }

        // Return the title of the most popular song
        return mostPopularSong.getTitle();
    }
}

