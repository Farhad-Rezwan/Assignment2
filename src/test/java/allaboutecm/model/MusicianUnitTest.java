package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class MusicianUnitTest {
    private Musician mus;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        mus = new Musician("Lucy Railton");
    }

    @Test
    @DisplayName("Musician name cannot be null and should throw null pointer exception")
    public void musicianNameCannotBeNull()
    {
        assertThrows(NullPointerException.class, () -> mus.setName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Musician name cannot be empty or blank")
    public void MusicianNameCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> mus.setName(arg));
    }

    @DisplayName("Should throw NullPointerException when albums are null")
    @Test
    public void shouldThrowExceptionWhenAlbumsAreNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> mus.setAlbums(null));
        assertEquals("Album list cannot be null", e.getMessage());
    }

    @DisplayName("Should accept proper Musician name")
    @ParameterizedTest
    @ValueSource(strings = {"Gianluigi Trovesi", "Gianni Coscia", "Herman Hupfeld"})
    public void shouldAcceptProperMusicianName(String args) {
        mus.setName(args);
        assertTrue(args.equals(mus.getName()));
    }

    @DisplayName("Should reject improper Musician name with multiple invalid letters")
    @ParameterizedTest
    @ValueSource(strings = {"1212", "@", "$", "_", "   F", "F   ", "f12"})
    public void shouldThrowIllegalArgumentExceptionWhenTrackNameIsSetALetter(String args)
    {
        assertThrows(IllegalArgumentException.class, () -> mus.setName(args));
    }



    @Test
    @DisplayName("should return a musician name")
    public void shouldGetMusicianName() throws IOException {
        Musician mus2 = new Musician("Lucy Railton");
        assertTrue(mus.getName().equals(mus2.getName()),"getName method for Musician class executed successfully");
    }

    //test cases for musician URL
    //Todo: With null argument, it should throw illegal argument exception


    @Test
    @DisplayName("If wrong musician URL is entered which is not responsive an exception should be thrown")
    public void shouldThrowUnknownHostExceptionWhenInvalidURLIsSet()
    {
        assertThrows(UnknownHostException.class, () -> mus
                .setMusicianUrl(new URL("https://www.goasdfasdfasdfaogle.com")));
    }

    @Test
    @DisplayName("the wiki website of a musician must have this musician name")
    public void wikiWebsiteContainsMusicianName()  {
        String wikiName = "";
        String wiki = mus.getWikiUrl().toString();
        String[] name1 = mus.getName().split(" ");
        for(int i=0; i < name1.length; i++)
        {
            wikiName += name1[i];
            if( i < name1.length-1)
            {
                wikiName += "_";
            }
        }
        assertNotEquals(-1, wiki.indexOf(wikiName));
    }




    @ParameterizedTest
    @ValueSource(strings = {"https://www.google.com/"})
    public void shouldReturnRightFansWebsite(String arg) {
        URL url = null;
        try {
            url = new URL(arg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mus.setFansWebsite(url);
        assertEquals(url, mus.getFansWebsite());
    }
}



