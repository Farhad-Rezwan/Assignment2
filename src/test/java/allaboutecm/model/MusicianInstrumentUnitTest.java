package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MusicianInstrumentUnitTest {

    private MusicianInstrument musicianInstrument;

    @BeforeEach
    //initialize
    public void setUp() throws IOException {
        Musician musician = new Musician("Bill White");
        MusicalInstrument mi = new MusicalInstrument("Piano");
        //new set<MusicalInstrument> MusicalInstrument = new HashSet<>()
        Set<MusicalInstrument> musicalInstrumentList = new HashSet<>();
        musicalInstrumentList.add(mi);
        musicianInstrument = new MusicianInstrument( musician, musicalInstrumentList);
    }

    @Test
    @DisplayName("musician cannot be null")
    public void musicianCannotBeNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> musicianInstrument.setMusician(null));
        assertEquals("Musician can not be null", e.getMessage());
        e.getMessage();
    }

    @Test
    @DisplayName("musical Instrument cannot be null")
    public void musicalInstrumentCannotBeNull() {
        NullPointerException e =  assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstruments(null));
        assertEquals("MusicalInstrument can not be null", e.getMessage());
    }

    @Test
    @DisplayName("Same Musician And Musical Instrument Means Same Musician Instrument")
    public void sameMusicianAndMusicalInstrumentMeansSameMusicianInstrument() throws IOException{
        Musician musician1 = new Musician("Bill White");
        MusicalInstrument mi1 = new MusicalInstrument("Piano");
        Set<MusicalInstrument> musicalInstrumentList1 = new HashSet<MusicalInstrument>();
        musicalInstrumentList1.add(mi1);
        MusicianInstrument musicianInstrument1 = new MusicianInstrument( musician1, musicalInstrumentList1);
        assertEquals(musicianInstrument, musicianInstrument1);
    }

    @Test
    @DisplayName("should return a musician as an object")
    public void shouldGetMusician() throws IOException{
        Musician musician1 = new Musician("Bill White");
        assertTrue(musicianInstrument.getMusician().equals(musician1),"getMusician method execute successfully");
    }

    @Test
    @DisplayName("should return a musicalInstrument list as set<musicalInstrument>")
    public void shouldGetMusicalInstrument() {
        MusicalInstrument mi1 = new MusicalInstrument("Piano");
        Set<MusicalInstrument> musicalInstrumentList1 = new HashSet<MusicalInstrument>();
        musicalInstrumentList1.add(mi1);
        assertTrue(musicianInstrument.getMusicalInstruments().equals(musicalInstrumentList1),"getMusicalInstruments method execute successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"wdz ghm","    abc defg    "})
    public void inputNameFormatWithNoSpecialCharOfMusician(String arg){
        boolean input = musicianInstrument.nameInput(arg);
        assertTrue(input, "The input musician name has illegal character. ");
    }


}
