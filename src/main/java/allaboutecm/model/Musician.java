package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An artist that has been featured in (at least) one ECM record.
 *
 * See {@https://www.ecmrecords.com/artists/1435045745}
 */
@NodeEntity
public class Musician extends Entity {
    @Property(name="name")
    private String name;

    @Convert(URLConverter.class)
    @Property(name="musicianURL")
    private URL musicianUrl;
    //new
    private URL wikiUrl;
    //end

    @Relationship(type="albums")
    private Set<Album> albums;

    //new
    private String briefBiography;
    private URL fansWebsite;

    //end

    public Musician() {
    }

    public Musician(String name) throws MalformedURLException {

        if (name==null) {
            throw new NullPointerException("musician name cannot be null or empty");
        }
        // Musician name cannot contain space at the beginning
        if (!name.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
            throw new IllegalArgumentException("Not a valid musician name");
        }
        notBlank(name);
        notNull(name);

        this.name = name;
        this.musicianUrl = null;
        this.briefBiography = briefBiography;
        String url= "https://en.wikipedia.org/wiki/" + name.replace(" ","_");
        wikiUrl = new URL (url);


        //new
//        String url= "https://en.wikipedia.org/wiki/" + name.replace(" ","_");
//        try{
//            wikiUrl = new URL(url);
//        }catch (MalformedURLException e){
//            throw new IllegalArgumentException("Not a valid url");
//        }
        //end
        albums = Sets.newHashSet();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {


        if (name==null) {
            throw new NullPointerException("musician name cannot be null or empty");
        }
        // Musician name cannot contain space at the beginning
        if (!name.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
            throw new IllegalArgumentException("Not a valid musician name");
        }

        notNull(name);
        notBlank(name);
        this.name = name;

    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        if (null == albums){
            throw new NullPointerException("Album list cannot be null");
        }
        this.albums = albums;


        this.albums = albums;
    }

    public URL getMusicianUrl() {
        return musicianUrl;
    }

    public void setMusicianUrl(URL musicianUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)musicianUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() == 401)
        {
            throw new UnknownHostException("album URL is invalid");
        }
        this.musicianUrl = musicianUrl;
    }

    //new
    public URL getFansWebsite() {
        return fansWebsite;
    }
    public void setFansWebsite(URL fansWebsite) {
        this.fansWebsite = fansWebsite;
    }
    public String getBriefBiography()  {
        return briefBiography;
    }

    public void setBriefBiography(String briefBiography)  {
        this.briefBiography = briefBiography;
    }

    public URL getWikiUrl()  {
        return wikiUrl;
    }

    public void setWikiUrl(URL musicianUrl)  {
        this.wikiUrl = wikiUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Musician that = (Musician) o;
        return Objects.equals(name, that.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}
