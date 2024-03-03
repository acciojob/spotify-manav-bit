package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;
    private boolean like=false;
    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
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
        for(User user:users){
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        User newuser=new User(name,mobile);
        users.add(newuser);
        return newuser;
    }

    public Artist createArtist(String name) {
        for(Artist a:artists){
            if(a.getName().equals(name)){
                return a;
            }
        }
        Artist newartist=new Artist(name);
        artists.add(newartist);
        return newartist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist=null;
        for(Artist a:artists){
            if(a.getName().equals(artistName)){
                artist=a;
                break;
            }
        }
        //if artist not exist
        if(artist==null){
            artist=createArtist(artistName);
        }
        Album newAlbum=new Album(title);
        if(!albums.contains(newAlbum)){
            albums.add(newAlbum);
        }
        if(artistAlbumMap.containsKey(artist)){
            List<Album> allAlbums=artistAlbumMap.get(artist);
            boolean c=false;
            for(Album a:allAlbums){
                if(a.getTitle().equals(title)){
                    c=true;
                    break;
                }
            }
            if(!c){
                allAlbums.add(newAlbum);
            }
        }
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album a=null;
        for(Album b:albums){
            if(b.getTitle().equals(albumName)){
                a=b;
                break;
            }
        }
        Song newsong = new Song(title, length);
        if(a==null){
            throw new Exception("Album does not exist");
        }

        else {

            List<Song> songlist =albumSongMap.get(a);
            songlist.add(newsong);

        }
        return newsong;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //new playlist
        Playlist pl=new Playlist(title);
        playlists.add(pl);
        //new songs list for playlist
        List<Song> plsongs=new ArrayList<>();
        // add songs in songlist for playlist
        for(Song s:songs){
            if(s.getLength()==length){
                plsongs.add(s);
            }

        }
//map songlist with playlist name
        playlistSongMap.put(pl,plsongs);
        User u=null;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                u=user;
                break;
            }
        }
        if(u==null){
            throw new Exception("user not exist");
        }

//map creator with playlist created
        creatorPlaylistMap.put(u,pl);
        //adding user as listener
        List<User> listener=new ArrayList<>();
        listener.add(u);
        // map listener and playlist
        playlistListenerMap.put(pl,listener);

        //mapping all playlists created by user
        List<Playlist> userplaylists=new ArrayList<>();
        userplaylists.add(pl);
        userPlaylistMap.put(u,userplaylists);


        return pl;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist pl=new Playlist(title);//created a playlist with given title
        playlists.add(pl);//added playlist to playlists
        List<Song> newsl=new ArrayList<>();
        for(String songTitle:songTitles){
            newsl.add(new Song(songTitle,0));
        }
        playlistSongMap.put(pl,newsl);
        User u=null;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                u=user;
                break;
            }
        }
        if(u==null){
            throw new Exception("user not exist");
        }
        //map creator with playlist created
        creatorPlaylistMap.put(u,pl);
        //adding user as listener
        List<User> listener=new ArrayList<>();
        listener.add(u);
        // map listener and playlist
        playlistListenerMap.put(pl,listener);
        //mapping all playlists created by user
        List<Playlist> userplaylists=new ArrayList<>();
        userplaylists.add(pl);
        userPlaylistMap.put(u,userplaylists);
return pl;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
User user=null;
//finding user
for(User u:users){
    if(u.getMobile().equals(mobile)){
        user=u;
        break;
    }
}
        if(user==null){
            throw new Exception("user not exist");

        }
        //finding playlist with given title
        Playlist temp=null;
        for(Playlist pl:playlists){
            if(pl.getTitle().equals(playlistTitle))
                temp=pl;
        }
        if(temp==null){
            throw new Exception("playlist not exist");

        }
        List<User>listener=new ArrayList<>();
        listener.add(user);
        playlistListenerMap.put(temp,listener);
        return temp;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        if(!like){
            like =true;
            User user=null;
            for(User u:users){
                if(u.getMobile().equals(mobile)){
                    user=u;
                }
            }
            if (user == null) {
                throw new Exception("User does not exist");
            }
            Song song=null;
            for(Song s:songs){
                if(s.getTitle().equals(songTitle)){
                    song=s;
                }
            }
            if (song == null) {
                throw new Exception("Song does not exist");
            }
            //created the list of users who like the song
            List<User> userss=new ArrayList<>();

            userss.add(user);//added the user in list who like the song
            songLikeMap.put(song,userss);// map the user who liked this song
            // increasing the like of the song
            int like= song.getLikes();
            song.setLikes(like+1);

            // increasing the like of artist who created this song

            for (Map.Entry<Album, List<Song>> entry : albumSongMap.entrySet()) {
                Album album = entry.getKey();
                List<Song> songsInAlbum = entry.getValue();
                if (songsInAlbum.contains(song)) {
                    // Album found, find the artist using the artistAlbumMap
                    for (Map.Entry<Artist, List<Album>> artistEntry : artistAlbumMap.entrySet()) {
                        List<Album> albumsByArtist = artistEntry.getValue();
                        if (albumsByArtist.contains(album)) {
                            // Artist found, increment the likes
                            Artist artist = artistEntry.getKey();
                            int artistLikes = artist.getLikes();
                            artist.setLikes(artistLikes + 1);
                            break;
                        }
                    }
                    break;
                }
            }
            return song;
        }
return null;
    }

    public String mostPopularArtist() {
        if (artists.isEmpty()) {
            return "No artists found";
        }
        int max=Integer.MIN_VALUE;
        String mostPopularArtist="";
        for(Artist s:artists){
            if(s.getLikes()>max){
                max=s.getLikes();
                mostPopularArtist=s.getName();
            }

        }
        return mostPopularArtist;
    }

    public String mostPopularSong() {
        if (songs.isEmpty()) {
            return "No songs found";
        }
        int max=Integer.MIN_VALUE;
        String mostPopularSong="";
        for(Song s:songs){
            if(s.getLikes()>max){
                max=s.getLikes();
                mostPopularSong=s.getTitle();
            }

        }
        return mostPopularSong;
    }
}
