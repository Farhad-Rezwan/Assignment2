package allaboutecm.mining;

import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Musician;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import java.io.IOException;

//package allaboutecm.dataaccess.neo4j;
import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//package allaboutecm.mining;

import allaboutecm.model.MusicianInstrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Set;


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


    // 1st Method
    @DisplayName("Should return the musician when there is only one")
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() throws IOException{
        Album album = new Album(1975, "ECM 1064/65", "The abcd Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(musician);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }



    @DisplayName("Should return two for most prolific musicians when parameter is two")
    @Test
    public void shouldReturnTwoForMostProlificMusicians() throws IOException{
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");

        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        musician3.setAlbums(Sets.newHashSet(album8));

        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);

        List<Musician> result = ecmMiner.mostProlificMusicians(2, -1, -1);

        //creating testResult array to compare with the result which is returned from mostProlificMusician method
        List<Musician> testResult = Lists.newArrayList();
        testResult.add(musician2);
        testResult.add(musician1);

        // checking whether it is returning the adequate number of K
        assertEquals(2,result.size());

        // comparing whether it is returning the valid result
        assertEquals(result,testResult);
    }

    @Test
    @DisplayName("Years for most prolific musician to get should be a valid year")
    public void yearsForMostProlificMusicianToGetShouldBeValidYear() throws IOException{
        Album album1 = new Album(1975, "ECM 1064/61", "The abcd Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));
        dao.createOrUpdate(musician1);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, 100,1122200));
        assertEquals("Years should be greater than 1970, not future, and valid year", e.getMessage());

        IllegalArgumentException f = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, 1990,200));
        assertEquals("Years should be greater than 1970, not future, and valid year", f.getMessage());

        IllegalArgumentException g = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, 2030,1990));
        assertEquals("Years should be greater than 1970, not future, and valid year", g.getMessage());

        IllegalArgumentException h = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, 2030,1990));
        assertEquals("Years should be greater than 1970, not future, and valid year", h.getMessage());
    }

    @DisplayName("mostProlificMusician method output should return in order form most to least prolific")
    @Test
    public void shouldReturnMostProlificMusicianInOrderFromMostToLeastProlific()throws IOException {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");

        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        musician3.setAlbums(Sets.newHashSet(album8));

        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);

        List<Musician> result = ecmMiner.mostProlificMusicians(2, 1971, 2019);

        // checking whether the most prolific musician is first in the array
        assertEquals(result.get(0), musician2);
        // Checking whether the second most prolific musician is second in the array
        assertEquals(result.get(1), musician1);
    }


    @DisplayName("should Return Most Talented Musician If He Can play most number of Instruments")
    @Test
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill()throws IOException {
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

    @DisplayName("should Return Most Talented Musician If He Has Most Instrument Skill2 here")
    @Test
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill2()throws IOException {
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

    @DisplayName("method mostTalentedMusicians should return an arrayList of musicians in proper order from the most talented to the least")
    @Test
    public void shouldReturnMostTalentedMusicianInOrderFromMostTalentedToLeast() throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");
        Musician musician4 = new Musician("Jack DeJohnette");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");
        MusicalInstrument mi4 = new MusicalInstrument("Piano");
        MusicalInstrument mi5 = new MusicalInstrument("Double Bass");

        /*
        from below data, here we can see that musician4 is the most talented, musician3 is second most, musician2 is
            third most and musician1 is least talented.
         so the order of return should be musician4 as first element in the arrayList musician3 as the second element of
            the arrayList and so on.
         */
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1,mi2,mi3,mi4));
        MusicianInstrument mnI4 = new MusicianInstrument(musician4, Sets.newHashSet(mi1,mi2,mi3,mi4, mi5));

        dao.createOrUpdate(mnI1);
        dao.createOrUpdate(mnI2);
        dao.createOrUpdate(mnI3);
        dao.createOrUpdate(mnI4);


        // checking results whether it is returning in proper order or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(4);
        assertEquals(musicians.get(0), musician4);
        assertEquals(musicians.get(1), musician3);
        assertEquals(musicians.get(2), musician2);
        assertEquals(musicians.get(3), musician1);

    }

    @DisplayName("Musician Instrument count has to be based on unique instruments not duplicates even though" +
            "same instrument can appear in different MusicianInstrument object for one musician")
    @Test
    public void sameMusicalInstrumentInDifferentMusicianInstrumentForSameMusicianShouldNotImpactResult() throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");

        /*
        from the below data we can see musician1, involved with multiple MusicianInstrument objects(here 3), and those objects has
        overlapping musical instruments. So the mostTalentedMusician method should return musician2 as most talented, as
        he owns 3 unique instruments skill.
         */
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1));
        MusicianInstrument mnI2 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI3 = new MusicianInstrument(musician1, Sets.newHashSet(mi2));
        MusicianInstrument mnI4 = new MusicianInstrument(musician2, Sets.newHashSet(mi1, mi2, mi3));


        dao.createOrUpdate(mnI1);
        dao.createOrUpdate(mnI2);
        dao.createOrUpdate(mnI3);
        dao.createOrUpdate(mnI4);

        // checking results wheter it is returning in proper order or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(musicians.get(0), musician2);
        assertEquals(musicians.get(1), musician1);

    }

    @DisplayName("for mostTalentedMusicians method whenever multiple musician has same number of instrument skill " +
            "should return those musician in any order")
    @Test
    public void shouldReturnMusicianInAnyOrderWhenMusiciansHasSameNumberOfInstrumentSkill() throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");
        Musician musician4 = new Musician("VCourtois");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");
        MusicalInstrument mi4 = new MusicalInstrument("dion");
        MusicalInstrument mi5 = new MusicalInstrument("dionsdfsd");

        /*
           here we can see form the below data, musician1 and musician2 has same number of instrument skills.
         */

        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1,mi2,mi3,mi4));
        MusicianInstrument mnI4 = new MusicianInstrument(musician4, Sets.newHashSet(mi1,mi2,mi3,mi4,mi5));

        dao.createOrUpdate(mnI1);
        dao.createOrUpdate(mnI2);
        dao.createOrUpdate(mnI3);
        dao.createOrUpdate(mnI4);

        // checking the result with different number for parameter k, and respective talented musician returned or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(4);
        List<Musician> musiciansWithSameNumberInstrumentSkill = new ArrayList<>();
        musiciansWithSameNumberInstrumentSkill.add(musician1);
        musiciansWithSameNumberInstrumentSkill.add(musician2);

        assertEquals(musicians.get(0), musician4);
        assertEquals(musicians.get(1), musician3);

        assertTrue(musiciansWithSameNumberInstrumentSkill.contains(musicians.get(2)));
        assertTrue(musiciansWithSameNumberInstrumentSkill.contains(musicians.get(3)));
    }


    @DisplayName("should Return The Musician When There Is One Only")
    @Test
    public void shouldReturnTheMusicianWhenThereIsOneOnly() throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        album1.setFeaturedMusicians(list1);

        dao.createOrUpdate(album1);
        List<Musician> result = ecmMiner.mostSocialMusicians(3);

        assertEquals(1,result.size());
        assertTrue(result.contains(musician1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number to return for most talented musician should be bigger than 0")
    public void socialMusicianNumberToGetAsParameterHasToBeMoreThanZero(int arr)throws IOException {
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        album1.setFeaturedMusicians(list1);

        dao.createOrUpdate(album1);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(arr));
        assertEquals("number of most social musician to return should be more than 0", e.getMessage());
    }

    @DisplayName("Should return the most social musician in ordered manner in arrayList")
    @Test
    public void shouldReturnTheMusicianArrangedFromMostToLeastSocial() throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");
        Musician musician4 = new Musician("Sarah Murcia");
        Musician musician5 = new Musician("Ziv Ravitz");
        Musician musician6 = new Musician("Daniel Erdmann");
        Musician musician7 = new Musician("Robin Fincker");
        Musician musician8 = new Musician("Stefano Battaglia");
        Musician musician9 = new Musician("Michael Gassmann");

        /* Here we can see musician6 has the most works( = 6) with musician7, musician5, musician4, musician3, musician2, musician1
                     musician5 has the second most works( = 5) with musician6, musician8, musician4, musician3, musician2
                     musician4 has the third most works( = 4) with musician6, musician5, musician9,  msician3
                     musician3 has the fourth most works( = 3) with musician6, musician5, musician4
                     musician2 has the fifth most work( = 2) with musician6, musician5
        */
        List<Musician> list1 = Lists.newArrayList(musician6, musician7);
        List<Musician> list2 = Lists.newArrayList(musician6, musician5);
        List<Musician> list3 = Lists.newArrayList(musician6, musician4);
        List<Musician> list4 = Lists.newArrayList(musician6, musician3);
        List<Musician> list5 = Lists.newArrayList(musician6, musician2);
        List<Musician> list6 = Lists.newArrayList(musician6, musician1);
        List<Musician> list7 = Lists.newArrayList(musician5, musician8);
        List<Musician> list8 = Lists.newArrayList(musician5, musician4);
        List<Musician> list9 = Lists.newArrayList(musician5, musician3);
        List<Musician> list10 = Lists.newArrayList(musician5, musician2);
        List<Musician> list11 = Lists.newArrayList(musician4, musician9);
        List<Musician> list12 = Lists.newArrayList(musician4, musician3);

        //  The above lists are assigned to different albums
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        album1.setFeaturedMusicians(list1);
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        album2.setFeaturedMusicians(list2);
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        album3.setFeaturedMusicians(list3);
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        album4.setFeaturedMusicians(list4);
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        album5.setFeaturedMusicians(list5);
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        album6.setFeaturedMusicians(list6);
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        album7.setFeaturedMusicians(list7);
        Album album8 = new Album(2017, "ECM 1064/397030", "Bands Originals");
        album8.setFeaturedMusicians(list8);
        Album album9 = new Album(1999, "ECM 1064/10", "JeanLuc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1064/1668", "JOHANN HEINRICH SCHMELZER UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1064/1667", "FRANZ SCHUBERT KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1064/1591", "ARVO PART ALINA");
        album12.setFeaturedMusicians(list12);




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

        List<Musician> result = ecmMiner.mostSocialMusicians(5);

        assertEquals(5,result.size());


        /* results sorted from highest to lowest....musician6 with 6, musician5 with 5, musician4 with 4 musician3 with 3
        and musician2 with2
         */
        assertEquals(result.get(0), (musician6));
        assertEquals(result.get(1), (musician5));
        assertEquals(result.get(2), (musician4));
        assertEquals(result.get(3), (musician3));
        assertEquals(result.get(4), (musician2));
    }

    @DisplayName("Those musician who have same number of other musicians they worked in albums" +
            "should be returned in any order")
    @Test
    public void shouldReturnTheMusicianInAnyOrderForThoseWhoHasWorkedInSameNumberOfDifferentMusicians()throws IOException {
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");
        Musician musician4 = new Musician("Sarah Murcia");
        Musician musician5 = new Musician("Ziv Ravitz");
        Musician musician6 = new Musician("Daniel Erdmann");
        Musician musician7 = new Musician("Robin Fincker");
        Musician musician8 = new Musician("Stefano Battaglia");
        Musician musician9 = new Musician("Michael Gassmann");

        /*
           From below data we can see musician1, musician7, musician8, musician9 has the same number of other musicians( = 1) they worked.
                here musician1, musician7, musician8 and musician9 has worked with musician6, musician6, musician5, musician4 respectively.
        */
        List<Musician> list1 = Lists.newArrayList(musician6, musician7);
        List<Musician> list2 = Lists.newArrayList(musician6, musician5);
        List<Musician> list3 = Lists.newArrayList(musician6, musician4);
        List<Musician> list4 = Lists.newArrayList(musician6, musician3);
        List<Musician> list5 = Lists.newArrayList(musician6, musician2);
        List<Musician> list6 = Lists.newArrayList(musician6, musician1);
        List<Musician> list7 = Lists.newArrayList(musician5, musician8);
        List<Musician> list8 = Lists.newArrayList(musician5, musician4);
        List<Musician> list9 = Lists.newArrayList(musician5, musician3);
        List<Musician> list10 = Lists.newArrayList(musician5, musician2);
        List<Musician> list11 = Lists.newArrayList(musician4, musician9);
        List<Musician> list12 = Lists.newArrayList(musician4, musician3);

        //  The above lists are assigned to different albums
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        album1.setFeaturedMusicians(list1);
        Album album2 = new Album(2020, "ECM 1998/2617", "RIVAGES");
        album2.setFeaturedMusicians(list2);
        Album album3 = new Album(2019, "ECM 1998/2645", "Characters on a Wall");
        album3.setFeaturedMusicians(list3);
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        album4.setFeaturedMusicians(list4);
        Album album5 = new Album(2020, "ECM 1998/2680", "Big Vicious");
        album5.setFeaturedMusicians(list5);
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        album6.setFeaturedMusicians(list6);
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        album7.setFeaturedMusicians(list7);
        Album album8 = new Album(2017, "RJAL 1064/397030", "Bands Originals");
        album8.setFeaturedMusicians(list8);
        Album album9 = new Album(1999, "ECM 1064/10", "JeanLuc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1064/1668", "JOHANN HEINRICH SCHMELZER UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1064/1667", "FRANZ SCHUBERT KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1591", "ARVO PART ALINA");
        album12.setFeaturedMusicians(list12);

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

        List<Musician> result = ecmMiner.mostSocialMusicians(9);

        assertEquals(9,result.size());


        /* results sorted from highest to lowest....musician6 with 6, musician5 with 5, musician4 with 4 musician3 with 3
        and musician2 with2
         */
        assertEquals(result.get(0), (musician6));
        assertEquals(result.get(1), (musician5));
        assertEquals(result.get(2), (musician4));
        assertEquals(result.get(3), (musician3));
        assertEquals(result.get(4), (musician2));

        //all these musicians(musician1, musician7, musician8 and musician2) has worked with one contributor, they are assigned in any order.
        List<Musician> sameResult = Lists.newArrayList(musician1, musician7, musician8, musician9);

        assertTrue(sameResult.contains(result.get(5)));
        assertTrue(sameResult.contains(result.get(6)));
        assertTrue(sameResult.contains(result.get(7)));
        assertTrue(sameResult.contains(result.get(8)));


    }

    @DisplayName("should Return The Business Year When There Are Only Two")
    @Test
    public void shouldReturnTheBusinessYearWhenThereAreOnlyTwo() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);

        List<Integer> result = ecmMiner.busiestYears(3);

        assertEquals(2,result.size());
        assertTrue(result.contains(1977));
        assertTrue(result.contains(1976));
    }


    @DisplayName("should Return The Busiest Year")
    @Test
    public void shouldReturnTheBusiestYear() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");
        Album album7 = new Album(1977, "ECM 1064/67", "Horse");


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        dao.createOrUpdate(album7);

        //when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6,album7));
        List<Integer> result = ecmMiner.busiestYears(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(1977));
    }


    @DisplayName("should Return All Values In Descending Order of Busy years")
    @Test
    public void shouldReturnAllValuesInDescendingBusyOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");
        Album album7 = new Album(1977, "ECM 1064/67", "Horse");
        Album album8 = new Album(1978, "ECM 1064/68", "LOL");


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        dao.createOrUpdate(album7);
        dao.createOrUpdate(album8);


        List<Integer> result = ecmMiner.busiestYears(5);
        List<Integer> testResult = Lists.newArrayList();
        testResult.add(1977);
        testResult.add(1976);
        testResult.add(1978);
        assertEquals(3,result.size());
        assertEquals(result,testResult);

    }


    @DisplayName("returns Similar Album According To Musician")
    @Test
    public void returnsSimilarAlbumAccordingToMusician() throws IOException{
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        Musician musician1 = new Musician("Keith");
        Musician musician2 = new Musician("Wong");
        Musician musician3 = new Musician("Warrick");
        Musician musician4 = new Musician("Lemon");
        Musician musician5 = new Musician("Oligay");

        album1.setFeaturedMusicians(Lists.newArrayList(musician1));
        album3.setFeaturedMusicians(Lists.newArrayList(musician1));
        album2.setFeaturedMusicians(Lists.newArrayList(musician2));
        album4.setFeaturedMusicians(Lists.newArrayList(musician3));
        album5.setFeaturedMusicians(Lists.newArrayList(musician4));
        album6.setFeaturedMusicians(Lists.newArrayList(musician5));


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);



        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album3));
    }


    @DisplayName("return0 If No Album Similar With It")
    @Test
    public void return0IfNoAlbumSimilarWithIt() throws IOException{
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        Musician musician1 = new Musician("Keith");
        Musician musician2 = new Musician("Wong");
        Musician musician3 = new Musician("Warrick");
        Musician musician4 = new Musician("Lemon");
        Musician musician5 = new Musician("Oligay");

        album1.setFeaturedMusicians(Lists.newArrayList(musician1));
        album2.setFeaturedMusicians(Lists.newArrayList(musician2));
        album3.setFeaturedMusicians(Lists.newArrayList(musician3));
        album4.setFeaturedMusicians(Lists.newArrayList(musician4));
        album5.setFeaturedMusicians(Lists.newArrayList(musician5));

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);


        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);
        assertEquals(0,result.size());
    }



    @Test
    @DisplayName("Album cannot be null")
    public void AlbumCannotBeNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> ecmMiner.mostSimilarAlbums(1,null));
        assertEquals("Album can not be null", e.getMessage());
    }



    /*
    ---------     Method 6 (mostExpensiveAlbums)  ----------
    ---------          Extra Credit 1            -----------
    */



    @Test
    @DisplayName("should Return 0 When No Price Inside Database")
    public void shouldReturn0WhenNoPriceInsideDatabase() {
        List<Album> result = ecmMiner.mostExpensiveAlbums(3);
        assertEquals(0,result.size());
    }

    @Test
    @DisplayName("should Return The Most Expensive Price When There Are Only Two")
    public void shouldReturnTheMostExpensivePriceWhenThereAreOnlyTwo() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        album1.setPrice(999.99);
        album3.setPrice(100.99);

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        List<Album> result = ecmMiner.mostExpensiveAlbums(5);

        assertEquals(2,result.size());
        assertTrue(result.contains(album1));
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("should Return The Most Expensive Price Album")
    public void shouldReturnTheMostExpensivePrice() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        album1.setPrice(999.99);
        album3.setPrice(100.99);
        album4.setPrice(100.99);

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        List<Album> result = ecmMiner.mostExpensiveAlbums(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album1));
    }


    @Test
    @DisplayName("should Return All Albums InDescending PriceOrder")
    public void shouldReturnAllAlbumsInDescendingPriceOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        album1.setPrice(999.99);
        album2.setPrice(888.88);
        album3.setPrice(666.66);
        album4.setPrice(111.11);
        album5.setPrice(0.0);


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);
        List<Album> result = ecmMiner.mostExpensiveAlbums(6);
        List<Album> testResult = Lists.newArrayList();
        testResult.add(album1);
        testResult.add(album2);
        testResult.add(album3);
        testResult.add(album4);
        testResult.add(album5);
        assertEquals(5,result.size());
        assertEquals(result,testResult);

    }
    /*
               ---------     Method 7 (highestRatedAlbums)  ----------
               ---------          Extra Credit 2            -----------
*/

    @Test
    @DisplayName("should return zero when no ratings inside database")
    public void shouldReturnZeroNumberOfAlbumsWhenNoAlbumRatingsAreThereInDatabase() {
        List<Album> result = ecmMiner.highestRatedAlbums(3);
        assertEquals(0,result.size());
    }

    @Test
    @DisplayName("Should return the  ratings when there are only two ratings are available")
    public void shouldReturnTheRatingsWhenOnlyTwoRatingsAreAvailable() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");

        album1.setRating(4.5);
        album3.setRating(3.0);


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);

        List<Album> result = ecmMiner.highestRatedAlbums(5);

        assertEquals(2,result.size());
        assertTrue(result.contains(album1));
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("Should return the highest rated album")
    public void shouldReturnTheHighestRatedAlbum() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");

        album1.setRating(5.0);
        album3.setRating(4.5);
        album4.setRating(4);


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);

        List<Album> result = ecmMiner.highestRatedAlbums(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album1));
    }


    @Test
    @DisplayName("Should return the highest rated albums in ordered manner")
    public void shouldReturnHighestRatedAlbumsInProperOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");


        album1.setRating(5.0);
        album2.setRating(4.0);
        album3.setRating(3.0);
        album4.setRating(2.0);
        album5.setRating(1.0);


        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);


        List<Album> result = ecmMiner.highestRatedAlbums(6);
        assertEquals(5,result.size());


        assertEquals(result.get(0), album1);
        assertEquals(result.get(1), album2);
        assertEquals(result.get(2), album3);
        assertEquals(result.get(3), album4);
        assertEquals(result.get(4), album5);

    }

    @DisplayName("mostTalentedMusicians method should return one when there is only one musician in data")
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOneForTalentInInstrument()throws IOException {
        Musician musician1 = new Musician("Keith Jarrett");
        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Accordion");
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));

        dao.createOrUpdate(mnI1);


        List<Musician> musicians = ecmMiner.mostTalentedMusicians(10);

        assertTrue(musicians.contains(musician1));

        /*
         checking whether the mostTalentedMusicians method returns array size of 1 or not
         or,
         checking whether the mostTalentedMusicians method returning only One musician or not.
        */
        assertEquals(1, musicians.size());
    }



    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number to return for most talented musician should be bigger than 0")
    public void talentedMusicianNumberAsParameterHasToBeMoreThanZero(int arr) throws IOException{
        Musician musician1 = new Musician("Keith Jarrett");
        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Accordion");
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));

        dao.createOrUpdate(mnI1);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostTalentedMusicians(arr));
        assertEquals("number of most talented musician to return should be more than 0", e.getMessage());
    }






    @DisplayName("mostProlificMusician method output should return in order form most to least prolific")
    @Test
    public void shouldReturnInAnyOrderWhenTwoMusiciansAreSameProlific() throws IOException{
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");

        // Here all the musician has same number of album involvement
        musician1.setAlbums(Sets.newHashSet(album1));
        musician2.setAlbums(Sets.newHashSet(album2));


        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);

        List<Musician> result = ecmMiner.mostProlificMusicians(2, 1971, 2020);



        // creating testResult array to compare with the result which is returned from mostProlificMusician method
        List<Musician> sameResult = Lists.newArrayList();
        sameResult.add(musician2);
        sameResult.add(musician1);

        // mostProlificMusician method should return array size of two or two musicians.
        assertEquals(2, result.size());

        // result can come in any order as the musicians are similar prolific in regards to number of album count.
        assertTrue(sameResult.contains(result.get(0)));
        assertTrue(sameResult.contains(result.get(1)));

    }



    //new
    @DisplayName("Start year must be smaller than end year")
    @ParameterizedTest
    @ValueSource(ints = {2005, 2010})
    public void prolificMusicianStartYearShouldSmallerThanEndYear(int arr)throws IOException {
        Album album1 = new Album(1975, "ECM 1064/61", "The Koln Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));

        dao.createOrUpdate(musician1);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, arr,1999));
        assertEquals("Start year should smaller than end year", e.getMessage());
    }


    //new
    @DisplayName("Start year must be smaller than end year")
    @ParameterizedTest
    @ValueSource(ints = {2005, 2010})
    public void startYearBiggerThanEndYearWithCreateDaoMethod(int arg) throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        dao.createOrUpdate(musician);
        List<Musician> mostProlific = ecmMiner.mostProlificMusicians(2, 2005, arg);
        assertEquals(0, mostProlific.size());
    }

    //new-- changed k value which can be negative in ECM class and start year for this code had to be changed to 1970
    //as our code made sure that a year which is entered is greater than 1970



    @Test
    @DisplayName("if not album‘s release year between the input start year and endyear will return empty list")
    public void shouldReturnEmptyListWhenReleaseYearNotBetweenStartYearAndEndYearWithCreateDaoMethod() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        Album album1 = new Album(2019, "ECM 12344", "Great");
        Musician musician1 = new Musician("Mia");
        Album album2 = new Album(2008, "ECM 13456", "Good");
        Album album3 = new Album(2006, "ECM 123456", "Duang duang duang");
        Musician musician2 = new Musician("Bob");
        Set<Album> albumList1 = Sets.newHashSet(album);
        Set<Album> albumList2= Sets.newHashSet(album,album2, album1);
        Set<Album> albumList3= Sets.newHashSet(album1,album2, album3);
        musician.setAlbums(albumList1);
        musician1.setAlbums(albumList2);
        musician2.setAlbums(albumList3);
        dao.createOrUpdate(musician);
        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        List<Musician> mostProlific = ecmMiner.mostProlificMusicians(1, 1976, 2005);
        assertEquals(0, mostProlific.size());
    }


    @Test
    public void KValueBiggerThanListSizeForTalentedMusiciansWithCreateDaoMethod() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        Musician musician1 = new Musician("Mia");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        MusicalInstrument musicalInstrument1 = new MusicalInstrument("Violin");
        MusicalInstrument musicalInstrument2 = new MusicalInstrument("Accordion");
        MusicianInstrument mi = new MusicianInstrument(musician, Sets.newHashSet(musicalInstrument,musicalInstrument1,musicalInstrument2 ));
        MusicianInstrument mi1 = new MusicianInstrument(musician1, Sets.newHashSet(musicalInstrument,musicalInstrument1));
        dao.createOrUpdate(mi1);
        dao.createOrUpdate(mi);
        List<Musician> mostTalented = ecmMiner.mostTalentedMusicians(5);
        assertEquals(2, mostTalented.size());
    }

    @Test
    public void findTalentedMusiciansWithCreateDaoMethod() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        Musician musician1 = new Musician("Mia");
        Musician musician2 = new Musician("Bob");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        MusicalInstrument musicalInstrument1 = new MusicalInstrument("Violin");
        MusicalInstrument musicalInstrument2 = new MusicalInstrument("Accordion");
        MusicianInstrument mi = new MusicianInstrument(musician, Sets.newHashSet(musicalInstrument,musicalInstrument1,musicalInstrument2 ));
        MusicianInstrument mi1 = new MusicianInstrument(musician1, Sets.newHashSet(musicalInstrument,musicalInstrument1));
        MusicianInstrument mi2 = new MusicianInstrument(musician2, Sets.newHashSet(musicalInstrument));
        dao.createOrUpdate(mi1);
        dao.createOrUpdate(mi);
        dao.createOrUpdate(mi2);
        List<Musician> mostTalented = ecmMiner.mostTalentedMusicians(2);
        assertEquals(2, mostTalented.size());
        assertTrue(mostTalented.contains(musician) && mostTalented.contains(musician1));
    }

    @Test
    public void KValueBiggerThanListSizeWithCreateDaoMethod(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2003, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        List<Integer> releasedYear = ecmMiner.busiestYears(10);
        assertEquals(3,releasedYear.size());
    }

    @Test
    public void KValueSmallerThanListSizeWithCreateDaoMethod(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2017, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        List<Integer> releasedYear = ecmMiner.busiestYears(2);
        assertEquals(2,releasedYear.size());
        List<Integer> checkYear = Lists.newArrayList(2017,1975);
        assertEquals(checkYear,releasedYear);
    }



}
