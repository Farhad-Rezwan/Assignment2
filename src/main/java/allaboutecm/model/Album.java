package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.*;

/**
 * Represents an album released by ECM records.
 *
 * See {@https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett}
 */
@NodeEntity
public class Album extends Entity {

    @Property(name="releaseYear")
    private int releaseYear;

    @Property(name="recordNumber")
    private String recordNumber;

    @Property(name="albumName")
    private String albumName;

    /**
     * CHANGE: instead of a set, now featuredMusicians is a list,
     * to better represent the order in which musicians are featured in an album.
     */
    @Relationship(type="featuredMusicians")
    private List<Musician> featuredMusicians;

    @Relationship(type="instruments")
    private Set<MusicianInstrument> instruments;

    @Convert(URLConverter.class)
    @Property(name="albumURL")
    private URL albumURL;

    @Property(name="tracks")
    private List<String> tracks;


    @Property(name="rating")
    private Double rating;

    @Property(name="price")
    private Double price;

    private int sales;
    private int timeLength;
    private String genre;
    private String style;
    private String releaseFormat;
    private String reviews;
    private List<Musician> musicianGroup;

    public Album() {
    }

    public Album(int releaseYear, String recordNumber, String albumName) {
        notNull(recordNumber);
        notNull(albumName);

        notBlank(recordNumber);
        notBlank(albumName);
        //new
        //record number
        String[] prefix = {"ECM ","Carmo ", "RJAL ", "YAN ", "Watt ", "XtraWatt "};
        // loops through the prefixes
        for (int i = 0; i < 6; i++) {
            if (recordNumber.startsWith(prefix[i])){
                // replacing the recordNumber which can have / like `12/2` to 122
                String numberValue = recordNumber.substring(prefix[i].length())
                        .replaceAll("/","");

                // making sure that 122 is a digit
                if (Character.isDigit(Integer.parseInt(numberValue))){
                    throw new IllegalArgumentException("Illegal record number");
                }
            }

            // checking whether the recordNumber is alphanumeric or not, can include space and/or `/`
            if (!StringUtils.isAlphanumeric(recordNumber
                    .replaceAll("/","")
                    .replaceAll("\\s+",""))){
                throw new IllegalArgumentException("Illegal record number");
            }
            if(!StringUtils.startsWithAny(recordNumber, prefix)) {
                throw new IllegalArgumentException("Illegal record number");
            }
        }

        //year
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if((releaseYear>1970) && releaseYear<= year)
            this.releaseYear = releaseYear;
        else
            throw new IllegalArgumentException("Year should be greater than 1970");

        // making sure album name can contain proper name, can include `'`, `-`,
        // and should not accept invalid one or multiple letters/numbers
        if (!albumName.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
            throw new IllegalArgumentException("Not a valid album name");
        }
        //end


        this.releaseYear = releaseYear;
        this.recordNumber = recordNumber;
        this.albumName = albumName;

        this.albumURL = null;

        featuredMusicians = Lists.newArrayList();
        instruments = Sets.newHashSet();
        tracks = Lists.newArrayList();

        // initialising with negative because price or rating can hold value of 0.0
        price = null;
        rating = null;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        // only predefined prefix are accepted
        String[] prefix = {"ECM ","Carmo ", "RJAL ", "YAN ", "Watt ", "XtraWatt "};
        if (null == recordNumber){
            throw new NullPointerException("Record Number can not be null");
        }
        // loops through the prefixes
        for (int i = 0; i < 6; i++) {
            if (recordNumber.startsWith(prefix[i])){
                // replacing the recordNumber which can have / like `12/2` to 122
                String numberValue = recordNumber.substring(prefix[i].length())
                        .replaceAll("/","");

                // making sure that 122 is a digit
                if (Character.isDigit(Integer.parseInt(numberValue))){
                    throw new IllegalArgumentException("Illegal record number");
                }
            }

            // checking whether the recordNumber is alphanumeric or not, can include space and/or `/`
            if (!StringUtils.isAlphanumeric(recordNumber
                    .replaceAll("/","")
                    .replaceAll("\\s+",""))){
                throw new IllegalArgumentException("Illegal record number");
            }
            if(!StringUtils.startsWithAny(recordNumber, prefix)) {
                throw new IllegalArgumentException("Illegal record number");
            }

        }


        notNull(recordNumber);
        notBlank(recordNumber);

        this.recordNumber = recordNumber;
    }

    public List<Musician> getFeaturedMusicians() {
        return featuredMusicians;
    }

    public void setFeaturedMusicians(List<Musician> featuredMusicians) {
        if (null == featuredMusicians){
            throw new NullPointerException("Featured musician list cannot be null");
        }
        this.featuredMusicians = featuredMusicians;
    }

    public Set<MusicianInstrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<MusicianInstrument> instruments) {
        if (null == instruments){
            throw new NullPointerException("Instruments list cannot be null");
        }
        this.instruments = instruments;
    }

    public URL getAlbumURL() {
        return albumURL;
    }

    public void setAlbumURL(URL albumURL) throws IOException {
        if (null == albumURL) {
            throw new NullPointerException("Album URL cannot be null");
        }
        HttpURLConnection connection = (HttpURLConnection)albumURL.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // to check whether the url is Unauthorized or not,
        if (connection.getResponseCode() == 401){
            throw new UnknownHostException("album URL is invalid");
        }
        if (!connection.getURL().getHost().contains("ecmrecords")) {
            throw new IllegalArgumentException();
        }

        // sets the album url when the HTTP response is healthy
        if (connection.getResponseCode() == 200) {
            this.albumURL = albumURL;
        }

    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        if (null == tracks) {
            throw new NullPointerException("Tracks list cannot be null");
        }
        for (String element : tracks) {

            // making sure track name can contain proper name, can include `'`, `-`,
            // and should not accept invalid one or multiple letters/numbers
            if (!element.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
                throw new IllegalArgumentException("Not a valid track name");
            }
        }

        this.tracks = tracks;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if((releaseYear>1970) && releaseYear<= year)
            this.releaseYear = releaseYear;
        else
            throw new IllegalArgumentException("Year should be greater than 1970");
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        if (null == albumName){
            throw new NullPointerException("album name cannot be null or empty");
        }

        // making sure album name can contain proper name, can include `'`, `-`,
        // and should not accept invalid one or multiple letters/numbers
        if (!albumName.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")) {
            throw new IllegalArgumentException("Not a valid album name");
        }
        notNull(albumName);
        notBlank(albumName);

        this.albumName = albumName;
    }



    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (null == price) {
            throw new NullPointerException("price value should not be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price should hold non negative numbers");
        }
        this.price = price;
    }

    public void setPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price should hold non negative numbers");
        }

        this.price = Double.valueOf((price));
    }




    public Double getRating() {
        return rating;
    }



    public void setRating(Double rating) {
        if (null == rating) {
            throw new NullPointerException("Rating value should not be null");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating should hold valid range");
        }
        this.rating = rating;
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating should hold valid range");
        }

        this.rating = Double.valueOf((rating));
    }

    public void setSales(int sales)
    {this.sales= sales;
    }

    public int getSales()
    {return sales;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return releaseYear == album.releaseYear &&
                recordNumber.equals(album.recordNumber) &&
                albumName.equals(album.albumName);
    }

    //new
    public List<Musician> getMusicianGroup() {
        return musicianGroup;
    }

    public void setMusicianGroup(List<Musician> musicianGroup) {
        notNull(musicianGroup);
        notEmpty(musicianGroup);
        for(Musician m : musicianGroup){
            if (StringUtils.isBlank(m.getName())){
                throw new IllegalArgumentException("Featured musicians cannot be null or blank");
            }
        }
        if (musicianGroup.isEmpty())
        {
            throw new NullPointerException("Featured musicians should be set");
        }
        this.musicianGroup = musicianGroup;
    }

    //new
    public boolean releaseYearCompare(int yearRelease){
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        if (yearRelease <= year && yearRelease >= 1950){
            return true;
        }
        else {
            return false;
        }
    }

    //new

    public  boolean checkRecordNumber(String record){
        if (Pattern.matches("^ECM .*", record)){
            return true;
        }
        else {
            return false;
        }
    }
    public int getTimeLength() {
        return timeLength;
    }
    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }
    public String getReleaseFormat() {
        return releaseFormat;
    }
    public void setReleaseFormat(String releaseFormat) {
        this.releaseFormat = releaseFormat;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }
    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseYear, recordNumber, albumName);
    }
}
