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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
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

    /*
           ---------     Method 1 (mostProlificMusicians)  ----------
     */
    @DisplayName("Should return the musician when there is only one for the most prolific musician")
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



    @DisplayName("Should return two for most prolific musicians when parameter is two")
    @Test
    public void shouldReturnTwoForMostProlificMusicians() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE: PASOLINI");
        Album album5 = new Album(2020, "ECM 2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 2659", "Promontire");
        Album album7 = new Album(2017, "ECM 2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");

        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        musician3.setAlbums(Sets.newHashSet(album8));


        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3));

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

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number to return for most prolific musician should be bigger than 0")
    public void prolificMusicianNumberAsParameterHasToBeMoreThanZero(int arr) {
        Album album1 = new Album(1975, "ECM 1064/61", "The Köln Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(arr, 1999,2020));
        assertEquals("number of most prolific musician to return should be more than 0", e.getMessage());
    }

    @Test
    @DisplayName("Years for most prolific musician to get should be a valid year")
    public void yearsForMostProlificMusicianToGetShouldBeValidYear() {
        Album album1 = new Album(1975, "ECM 1064/61", "The Köln Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

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
    public void shouldReturnMostProlificMusicianInOrderFromMostToLeastProlific() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE: PASOLINI");
        Album album5 = new Album(2020, "ECM 2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 2659", "Promontire");
        Album album7 = new Album(2017, "ECM 2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Vincent Courtois");

        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album3,album4,album5,album6,album7));
        musician3.setAlbums(Sets.newHashSet(album8));


        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3));

        List<Musician> result = ecmMiner.mostProlificMusicians(2, 1971, 2019);

        // checking whether the most prolific musician is first in the array
        assertEquals(result.get(0), musician2);
        // Checking whether the second most prolific musician is second in the array
        assertEquals(result.get(1), musician1);
    }

    @DisplayName("mostProlificMusician method output should return in order form most to least prolific")
    @Test
    public void shouldReturnInAnyOrderWhenTwoMusiciansAreSameProlific() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Koln Concert");
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");

        // Here all the musician has same number of album involvement
        musician1.setAlbums(Sets.newHashSet(album1));
        musician2.setAlbums(Sets.newHashSet(album2));


        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2));

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

    /*
               ---------     Method 2 (mostTalentedMusicians)  ----------
    */
    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number to return for most talented musician should be bigger than 0")
    public void talentedMusicianNumberAsParameterHasToBeMoreThanZero(int arr) {
        Musician musician1 = new Musician("Keith Jarrett");
        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Accordion");
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostTalentedMusicians(arr));
        assertEquals("number of most talented musician to return should be more than 0", e.getMessage());
    }


    @DisplayName("mostTalentedMusicians method should return one when there is only one musician in data")
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOneForTalentInInstrument() {
        Musician musician1 = new Musician("Keith Jarrett");
        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Accordion");
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(10);

        assertTrue(musicians.contains(musician1));

        /*
         checking whether the mostTalentedMusicians method returns array size of 1 or not
         or,
         checking whether the mostTalentedMusicians method returning only One musician or not.
        */
        assertEquals(1, musicians.size());
    }

    @DisplayName("Should return most talented musician if he knows most number of instruments")
    @Test
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill() {
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Avishai Cohen");
        Musician musician3 = new Musician("Courtois Hugiwin");

        MusicalInstrument mi1 = new MusicalInstrument("Trumpet");
        MusicalInstrument mi2 = new MusicalInstrument("Drums");
        MusicalInstrument mi3 = new MusicalInstrument("Accordion");

        /* these data shows that musician1 is the most talented, musician2 is the second most talented and musician3 is least
        talented musician with number of musical Instruments they know is 3, 2 and 1 respectively.
         */
        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1));
        MusicianInstrument mnI4 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi3));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1,mnI2,mnI3, mnI4));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(2, musicians.size());

        // as parameter for mostTalentedMusician is 2, should return the top 2 most talented musician which are musician1, and musician2
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));


    }


    @DisplayName("method mostTalentedMusicians should return the number of musician, in respect to the method parameter given k")
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

        /*
            if k = 1, musician4 will be return as he has most number(5) of known instrument
            if k = 2, musician 4, and musician3 will be returned (4)
            if k = 10, musician4, musician3, musician2(2 known instrument), musician1 (2 known instrument); all of them
                    will be returned
         */

        MusicianInstrument mnI1 = new MusicianInstrument(musician1, Sets.newHashSet(mi1,mi2));
        MusicianInstrument mnI2 = new MusicianInstrument(musician2, Sets.newHashSet(mi2,mi3));
        MusicianInstrument mnI3 = new MusicianInstrument(musician3, Sets.newHashSet(mi1,mi2,mi3,mi4));
        MusicianInstrument mnI4 = new MusicianInstrument(musician4, Sets.newHashSet(mi1,mi2,mi3,mi4,mi5));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1,mnI2,mnI3,mnI4));



        // checking the result with different number for parameter k, and respective talented musician returned or not
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
    public void shouldReturnMostTalentedMusicianInOrderFromMostTalentedToLeast() {
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

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1,mnI2,mnI3,mnI4));


        // checking results wheter it is returning in proper order or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(4);
        assertEquals(musicians.get(0), musician4);
        assertEquals(musicians.get(1), musician3);
        assertEquals(musicians.get(2), musician2);
        assertEquals(musicians.get(3), musician1);

    }

    @DisplayName("Musician Instrument count has to be based on unique instruments not duplicates even though" +
            "same instrument can appear in different MusicianInstrument object for one musician")
    @Test
    public void sameMusicalInstrumentInDifferentMusicianInstrumentForSameMusicianShouldNotImpactResult() {
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

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1, mnI2, mnI3, mnI4));


        // checking results wheter it is returning in proper order or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(musicians.get(0), musician2);
        assertEquals(musicians.get(1), musician1);

    }

    @DisplayName("for mostTalentedMusicians method whenever multiple musician has same number of instrument skill " +
            "should return those musician in any order")
    @Test
    public void shouldReturnMusicianInAnyOrderWhenMusiciansHasSameNumberOfInstrumentSkill() {
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

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1,mnI2,mnI3,mnI4));



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

    /*
               ---------     Method 3 (mostSocialMusicians)  ----------
    */

    @DisplayName("mostSocialMusicians method should return one when there is only one musician in data")
    @Test
    public void shouldReturnTheMusicianWhenThereIsOneOnly() {
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        album1.setFeaturedMusicians(list1);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Musician> result = ecmMiner.mostSocialMusicians(3);

        assertEquals(1,result.size());
        assertTrue(result.contains(musician1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number to return for most talented musician should be bigger than 0")
    public void socialMusicianNumberToGetAsParameterHasToBeMoreThanZero(int arr) {
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        album1.setFeaturedMusicians(list1);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));


        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(arr));
        assertEquals("number of most social musician to return should be more than 0", e.getMessage());
    }


    @DisplayName("Should return the most social musician in ordered manner in arrayList")
    @Test
    public void shouldReturnTheMusicianArrangedFromMostToLeastSocial() {
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
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");
        album2.setFeaturedMusicians(list2);
        Album album3 = new Album(2019, "ECM 2645", "Characters on a Wall");
        album3.setFeaturedMusicians(list3);
        Album album4 = new Album(2007, "ECM 1998/99", "RE: PASOLINI");
        album4.setFeaturedMusicians(list4);
        Album album5 = new Album(2020, "ECM 2680", "Big Vicious");
        album5.setFeaturedMusicians(list5);
        Album album6 = new Album(2020, "ECM 2659", "Promontire");
        album6.setFeaturedMusicians(list6);
        Album album7 = new Album(2017, "ECM 2504", "Asian Field Variations");
        album7.setFeaturedMusicians(list7);
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");
        album8.setFeaturedMusicians(list8);
        Album album9 = new Album(1999, "ECM 1706-10", "Jean-Luc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1668", "JOHANN HEINRICH SCHMELZER: UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1667", "FRANZ SCHUBERT: KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1591", "ARVO PART: ALINA");
        album12.setFeaturedMusicians(list12);



        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2, album3, album4, album5, album6, album7, album8, album9, album10, album11, album12));

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
    public void shouldReturnTheMusicianInAnyOrderForThoseWhoHasWorkedInSameNumberOfDifferentMusicians() {
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
        Album album2 = new Album(2020, "ECM 2617", "RIVAGES");
        album2.setFeaturedMusicians(list2);
        Album album3 = new Album(2019, "ECM 2645", "Characters on a Wall");
        album3.setFeaturedMusicians(list3);
        Album album4 = new Album(2007, "ECM 1998/99", "RE: PASOLINI");
        album4.setFeaturedMusicians(list4);
        Album album5 = new Album(2020, "ECM 2680", "Big Vicious");
        album5.setFeaturedMusicians(list5);
        Album album6 = new Album(2020, "ECM 2659", "Promontire");
        album6.setFeaturedMusicians(list6);
        Album album7 = new Album(2017, "ECM 2504", "Asian Field Variations");
        album7.setFeaturedMusicians(list7);
        Album album8 = new Album(2017, "RJAL 397030", "Bands Originals");
        album8.setFeaturedMusicians(list8);
        Album album9 = new Album(1999, "ECM 1706-10", "Jean-Luc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1668", "JOHANN HEINRICH SCHMELZER: UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1667", "FRANZ SCHUBERT: KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1591", "ARVO PART: ALINA");
        album12.setFeaturedMusicians(list12);



        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2, album3, album4, album5, album6, album7, album8, album9, album10, album11, album12));

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

    /*
               ---------     Method 4 (busiestYears)  ----------
    */

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("Busiest Years You Want should bigger than 0")
    public void BusiestYearsYouWantShouldBiggerThan0(int arr) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.busiestYears(arr));
        assertEquals("Busiest Years You Want should bigger than 0", e.getMessage());
    }

    @Test
    @DisplayName("should Return 0 When No Album In Database")
    public void shouldReturn0WhenNoAlbumInDatabase() {
        List<Integer> result = ecmMiner.busiestYears(3);
        assertEquals(0,result.size());
    }

    @Test
    @DisplayName("should Return The Business Year When There Are Only Two")
    public void shouldReturnTheBusinessYearWhenThereAreOnlyTwo() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Integer> result = ecmMiner.busiestYears(3);

        assertEquals(2,result.size());
        assertTrue(result.contains(1977));
        assertTrue(result.contains(1976));
    }

    @Test
    @DisplayName("should Return The Busiest Year")
    public void shouldReturnTheBusiestYear() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");
        Album album7 = new Album(1977, "ECM 1064/67", "Horse");

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6,album7));
        List<Integer> result = ecmMiner.busiestYears(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(1977));
    }


    @Test
    @DisplayName("should Return All Values In Descending Busy Order")
    public void shouldReturnAllValuesInDescendingBusyOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");
        Album album7 = new Album(1977, "ECM 1064/67", "Horse");
        Album album8 = new Album(1978, "ECM 1064/68", "LOL");

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6,album7,album8));
        List<Integer> result = ecmMiner.busiestYears(5);
        List<Integer> testResult = Lists.newArrayList();
        testResult.add(1977);
        testResult.add(1976);
        testResult.add(1978);
        assertEquals(3,result.size());
        assertEquals(result,testResult);

    }


    /*
               ---------     Method 5 (mostSimilarAlbums)  ----------
    */
    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("Similar Albums Number You Want should bigger than 0")
    public void AlbumsNumberOfSimilarAlbumYouWantShouldBiggerThan0(int arr) {
        Album album1 = new Album(1975, "ECM 1064/61", "The Köln Concert");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSimilarAlbums(arr,album1));
        assertEquals("Similar Albums Number You Want should bigger than 0", e.getMessage());
    }

    @Test
    @DisplayName("Album cannot be null")
    public void AlbumCannotBeNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> ecmMiner.mostSimilarAlbums(1,null));
        assertEquals("Album can not be null", e.getMessage());
    }

    @Test
    @DisplayName("returns Similar Album According To Musician")
    public void returnsSimilarAlbumAccordingToMusician() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
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

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("return 0 If No Album Similar With It")
    public void return0IfNoAlbumSimilarWithIt() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
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


        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);
        assertEquals(0,result.size());
    }
/*
               ---------     Method 6 (mostExpensivePrice)  ----------
    */

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("most Expensive Price You Want should bigger than 0")
    public void mostExpensivePriceYouWantShouldBiggerThan0(int arr) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostExpensivePrice(arr));
        assertEquals("Expensive Price You Want should bigger than 0", e.getMessage());
    }

    @Test
    @DisplayName("should Return 0 When No Price Inside Database")
    public void shouldReturn0WhenNoPriceInsideDatabase() {
        List<Album> result = ecmMiner.mostExpensivePrice(3);
        assertEquals(0,result.size());
    }

    @Test
    @DisplayName("should Return The Most Expensive Price When There Are Only Two")
    public void shouldReturnTheMostExpensivePriceWhenThereAreOnlyTwo() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        album1.setPrice(999.99);
        album3.setPrice(100.99);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostExpensivePrice(5);

        assertEquals(2,result.size());
        assertTrue(result.contains(album1));
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("should Return The Most Expensive Price Album")
    public void shouldReturnTheMostExpensivePrice() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
        Album album2 = new Album(1976, "ECM 1064/62", "Bill");
        Album album3 = new Album(1976, "ECM 1064/63", "White");
        Album album4 = new Album(1977, "ECM 1064/64", "TED");
        Album album5 = new Album(1977, "ECM 1064/65", "Broken");
        Album album6 = new Album(1977, "ECM 1064/66", "House");

        album1.setPrice(999.99);
        album3.setPrice(100.99);
        album4.setPrice(100.99);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostExpensivePrice(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album1));
    }


    @Test
    @DisplayName("should Return All Albums InDescending PriceOrder")
    public void shouldReturnAllAlbumsInDescendingPriceOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The Köln Concert");
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


        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostExpensivePrice(6);
        List<Album> testResult = Lists.newArrayList();
        testResult.add(album1);
        testResult.add(album2);
        testResult.add(album3);
        testResult.add(album4);
        testResult.add(album5);
        assertEquals(5,result.size());
        assertEquals(result,testResult);

    }
}
