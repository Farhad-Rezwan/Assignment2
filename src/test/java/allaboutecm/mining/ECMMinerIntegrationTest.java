package allaboutecm.mining;

import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Musician;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;

//package allaboutecm.dataaccess.neo4j;
import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.support.FileUtils;
import scala.collection.immutable.ListSet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
/**
 * TODO: perform integration testing of both ECMMiner and the DAO classes together.
 */
class ECMMinerIntegrationTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";

    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;
    private ECMMiner ecmMiner;


    @BeforeEach
    public void setUp() throws IOException {

        Configuration configuration = new Configuration.Builder().build();
        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(session);
        ecmMiner = new ECMMiner(dao);

        addAll();
    }

    @AfterEach
    public void tearDownEach() {
        session.purgeDatabase();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        session.purgeDatabase();
        session.clear();
        sessionFactory.close();
        }

    public void addAll() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE: PASOLINI");
        Album album5 = new Album(2020, "ECM 2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 2659", "Promontire");
        Album album7 = new Album(2017, "ECM 2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");
        Album album9 = new Album(1999, "ECM 1706-10", "Jean-Luc Godard");
        Album album10 = new Album(1999, "ECM 1668", "JOHANN HEINRICH SCHMELZER: UNARUM FIDIUM");
        Album album11 = new Album(1999, "ECM 1667", "FRANZ SCHUBERT: KLAVIERSTUCKE");
        Album album12 = new Album(1999, "ECM 1591", "ARVO PART: ALINA");

        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1,album2, album11));
        Musician musician2 = new Musician("Avishai Cohen");
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        Musician musician3 = new Musician("Vincent Courtois");
        musician3.setAlbums(Sets.newHashSet(album8, album12));
        Musician musician4 = new Musician("Sarah Murcia");
        musician4.setAlbums(Sets.newHashSet(album8));
        Musician musician5 = new Musician("Ziv Ravitz");
        musician5.setAlbums(Sets.newHashSet(album9));
        Musician musician6 = new Musician("Daniel Erdmann");
        musician6.setAlbums(Sets.newHashSet(album10));
        Musician musician7 = new Musician("Robin Fincker");
        musician7.setAlbums(Sets.newHashSet(album8));
        Musician musician8 = new Musician("Stefano Battaglia");
        musician8.setAlbums(Sets.newHashSet(album8, album2, album3, album4));
        Musician musician9 = new Musician("Michael Gassmann");
        musician9.setAlbums(Sets.newHashSet(album8, album12));

        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);
        dao.createOrUpdate(musician4);
        dao.createOrUpdate(musician5);
        dao.createOrUpdate(musician6);
        dao.createOrUpdate(musician7);
        dao.createOrUpdate(musician8);
        dao.createOrUpdate(musician9);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        dao.createOrUpdate(album7);
        dao.createOrUpdate(album8);
        dao.createOrUpdate(album9);
        dao.createOrUpdate(album10);
        dao.createOrUpdate(album11);
        dao.createOrUpdate(album12);


    }

    // 1st Method ************************************************
    /*@Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        dao.createOrUpdate(musician);

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        //assertTrue(musicians.contains(musician));
    }*/



    //****************************************
    @Test
    public void shouldReturnTwoForMostProlificMusicians() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");
        Album album7 = new Album(1977, "ECM 1064/67", "Horse");
        Album album8 = new Album(1978, "ECM 1064/68", "LOL");

        Musician musician1 = new Musician("Keith");
        Musician musician2 = new Musician("Wong");
        Musician musician3 = new Musician("Warrick");

        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        musician3.setAlbums(Sets.newHashSet(album8));


        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);

        List<Musician> result = ecmMiner.mostProlificMusicians(2, -1, -1);
        List<Musician> testResult = Lists.newArrayList();
        testResult.add(musician1);
        testResult.add(musician2);
        assertEquals(2,result.size());
        //assertEquals(result,testResult);

    }

    @Test
    public void shouldReturnTheMusicianWhenThereIsOneOnly() {
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        album1.setFeaturedMusicians(list1);

        dao.createOrUpdate(album1);
        //when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Musician> result = ecmMiner.mostSocialMusicians(3);

        assertEquals(1,result.size());
        assertTrue(result.contains(musician1));
    }

    @Test
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill() {
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Courtois Hugiwin");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");

        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1));
        MusicianInstrument mnI4 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi3));
        MusicianInstrument mnI5 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi3));

        dao.createOrUpdate(mnI1);
        dao.createOrUpdate(mnI2);
        dao.createOrUpdate(mnI3);
        dao.createOrUpdate(mnI4);
        dao.createOrUpdate(mnI5);

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);

        assertEquals(2, musicians.size());
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));


    }

    @Test
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill2() {
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");
        Musician musician4 = new Musician("VCourtois");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");
        MusicalInstrument mi4 = new MusicalInstrument("dion");
        MusicalInstrument mi5 = new MusicalInstrument("dionsdfsd");

        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1,mi2,mi3,mi4));
        MusicianInstrument mnI4 = new MusicianInstrument(musician4, Sets.newHashSet(mi1,mi2,mi3,mi4));
        MusicianInstrument mnI5 = new MusicianInstrument(musician4, Sets.newHashSet(mi1,mi2,mi5));

        dao.createOrUpdate(mnI1);
        dao.createOrUpdate(mnI2);
        dao.createOrUpdate(mnI3);
        dao.createOrUpdate(mnI4);


        List<Musician> musicians = ecmMiner.mostTalentedMusicians(1);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician4));

        musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(2, musicians.size());
        assertTrue(musicians.contains(musician3));
        assertTrue(musicians.contains(musician4));

        musicians = ecmMiner.mostTalentedMusicians(10);
        assertEquals(4, musicians.size());
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertTrue(musicians.contains(musician3));
        assertTrue(musicians.contains(musician4));
    }








}