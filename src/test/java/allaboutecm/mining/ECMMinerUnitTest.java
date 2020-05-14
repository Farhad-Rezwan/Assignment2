package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: perform unit testing on the ECMMiner class, by making use of mocking.
 */
class ECMMinerUnitTest {
    private DAO dao;
    private ECMMiner ecmMiner;

    @BeforeEach
    public void setUp() {

        dao = mock(Neo4jDAO.class);
        ecmMiner = new ECMMiner(dao);
    }


    // 1st Method
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }



    @Test
    public void shouldReturnFiveForMethod0() {
        Album album1 = new Album(1975, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1975, "ECM 1064/62", "The Köln Concer");
        Album album3 = new Album(1975, "ECM 1064/63", "The Köln Conct");
        Album album4 = new Album(1975, "ECM 1064/64", "The Köln Cert");
        Album album5 = new Album(1975, "ECM 1064/65", "The Köoncert");
        Album album6 = new Album(1975, "ECM 1064/66", "Tln Concert");
        Musician musician1 = new Musician("Keith Jarrett1");
        musician1.setAlbums(Sets.newHashSet(album1));
        Musician musician2 = new Musician("Keith Jarrett2");
        musician2.setAlbums(Sets.newHashSet(album2));
        Musician musician3 = new Musician("Keith Jarrett3");
        musician3.setAlbums(Sets.newHashSet(album3));
        Musician musician4 = new Musician("Keith Jarrett4");
        musician4.setAlbums(Sets.newHashSet(album4));
        Musician musician5 = new Musician("Keith Jarrett5");
        musician5.setAlbums(Sets.newHashSet(album5));
        Musician musician6 = new Musician("Keith Jarrett6");
        musician6.setAlbums(Sets.newHashSet(album6));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5,musician6));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(5, musicians.size());
        //assertTrue(musicians.contains(musician1));
    }





//    2nd Method

    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOneForTalentInInstrument() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician ("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(10);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }

    public void shouldReturnMostTallentedMusicianIfHeHasMostInstrumentSkill() {
        Album album = new Album(1975,"ECM 1064/65", "The Köln Concert");

        MusicianInstrument musicianInstrument1 = new MusicianInstrument(new Musician("Keith Jarrett"),
                Sets.newHashSet(new MusicalInstrument("Trumpet"), new MusicalInstrument("Effects"), new MusicalInstrument("Synthesizer")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(new Musician("Keith Jarrett"),
                Sets.newHashSet(new MusicalInstrument("Trumpet"), new MusicalInstrument("Guitar"), new MusicalInstrument("Bass")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(new Musician("Avishai Cohen"),
                Sets.newHashSet(new MusicalInstrument("Drums"), new MusicalInstrument("Live Sampling"), new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(new Musician("Avishai Cohen"),
                Sets.newHashSet(new MusicalInstrument("Accordion"), new MusicalInstrument("Clarinets"), new MusicalInstrument("Effects")));
        MusicianInstrument musicianInstrument5 = new MusicianInstrument(new Musician("Vincent Courtois"),
                Sets.newHashSet(new MusicalInstrument("Trumpet")));
        MusicianInstrument musicianInstrument6 = new MusicianInstrument(new Musician("Uzi Ramirez"),
                Sets.newHashSet(new MusicalInstrument("Clarinets"), new MusicalInstrument("Effects")));
        MusicianInstrument musicianInstrument7 = new MusicianInstrument(new Musician("Pat Metheny"),
                Sets.newHashSet(new MusicalInstrument("Synthesizer"), new MusicalInstrument("Guitar"), new MusicalInstrument("Bass")));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1, musicianInstrument2, musicianInstrument3, musicianInstrument4, musicianInstrument5, musicianInstrument6, musicianInstrument7));

    }


//    - provide 2-3 musician input and check who has played more number of musical instruments(should return only one value)
//    -should return all values from most talented to less talented
//    - duplicates ?
//    - k(parameter) value entered is right or not
//    - sort at the end




// 3rd Method




//    -Provide inputs for musician and albums they have played in and check who has played in more album than others(should return only one value)
//    -should return all values arranged from most to least (return sorted)












// 4th Method

    //     Provide multiple album and years as input and check the year where more albums have been released than others(should return only one value)


    @Test
    public void busiestYear() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "The Köln Concer");
        Album album3 = new Album(1976, "ECM 1064/63", "The Köln Conct");
        Album album4 = new Album(1977, "ECM 1064/64", "The Köln Cert");
        Album album5 = new Album(1977, "ECM 1064/65", "The Köoncert");
        Album album6 = new Album(1977, "ECM 1064/66", "Tln Concert");
        Musician musician13 = new Musician("Keith Jarrett13");
        album1.setFeaturedMusicians(Lists.newArrayList(musician13));
        album3.setFeaturedMusicians(Lists.newArrayList(musician13));
        Musician musician2 = new Musician("Keith Jarrett2");
        album2.setFeaturedMusicians(Lists.newArrayList(musician2));
        Musician musician4 = new Musician("Keith Jarrett4");
        album4.setFeaturedMusicians(Lists.newArrayList(musician4));
        Musician musician5 = new Musician("Keith Jarrett5");
        album5.setFeaturedMusicians(Lists.newArrayList(musician5));
        Musician musician6 = new Musician("Keith Jarrett6");
        album6.setFeaturedMusicians(Lists.newArrayList(musician6));

        //       when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician13,musician2,musician4,musician5,musician6));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Integer> result = ecmMiner.busiestYears(3);

        assertEquals(2,result.size());
        assertTrue(result.contains(1977));
        assertTrue(result.contains(1976));
    }



    //      should return all values in descending order

















//    Last Method 5th
    @Test
    public void returnsSimilarAlbumAccordingToMusician() {
        Album album1 = new Album(1975, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1975, "ECM 1064/62", "The Köln Concer");
        Album album3 = new Album(1975, "ECM 1064/63", "The Köln Conct");
        Album album4 = new Album(1975, "ECM 1064/64", "The Köln Cert");
        Album album5 = new Album(1975, "ECM 1064/65", "The Köoncert");
        Album album6 = new Album(1975, "ECM 1064/66", "Tln Concert");
        Musician musician13 = new Musician("Keith Jarrett13");
        album1.setFeaturedMusicians(Lists.newArrayList(musician13));
        album3.setFeaturedMusicians(Lists.newArrayList(musician13));
        Musician musician2 = new Musician("Keith Jarrett2");
        album2.setFeaturedMusicians(Lists.newArrayList(musician2));
        Musician musician4 = new Musician("Keith Jarrett4");
        album4.setFeaturedMusicians(Lists.newArrayList(musician4));
        Musician musician5 = new Musician("Keith Jarrett5");
        album5.setFeaturedMusicians(Lists.newArrayList(musician5));
        Musician musician6 = new Musician("Keith Jarrett6");
        album6.setFeaturedMusicians(Lists.newArrayList(musician6));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician13,musician2,musician4,musician5,musician6));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album3));
    }



}