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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void shouldReturnTheMusicianWhenThereIsOnlyOne()throws IOException {
        Album album = new Album(1975, "ECM 1064/65", "The abcd Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }



    @DisplayName("Should return two for most prolific musicians when parameter is two")
    @Test
    public void shouldReturnTwoForMostProlificMusicians() throws IOException{
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        Album album8 = new Album(2017, "ECM 1064/397030", "Bands Originals");

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
    public void prolificMusicianNumberAsParameterHasToBeMoreThanZero(int arr) throws IOException{
        Album album1 = new Album(1975, "ECM 1064/61", "The abcd Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(arr, 1999,2020));
        assertEquals("number of most prolific musician to return should be more than 0", e.getMessage());
    }

    @Test
    @DisplayName("Years for most prolific musician to get should be a valid year")
    public void yearsForMostProlificMusicianToGetShouldBeValidYear()throws IOException {
        Album album1 = new Album(1975, "ECM 1064/61", "The abcd Concert");
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
    public void shouldReturnMostProlificMusicianInOrderFromMostToLeastProlific()throws IOException {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");
        Album album7 = new Album(2017, "ECM 1064/2504", "Asian Field Variations");
        Album album8 = new Album(2017, "RJAL 1064/397030", "Bands Originals");

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
    public void shouldReturnInAnyOrderWhenTwoMusiciansAreSameProlific() throws IOException{
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
    public void talentedMusicianNumberAsParameterHasToBeMoreThanZero(int arr) throws IOException{
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
    public void shouldReturnTheMusicianWhenThereIsOnlyOneForTalentInInstrument() throws IOException{
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
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill() throws IOException{
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
    public void shouldReturnMostTalentedMusicianIfHeHasMostInstrumentSkill2() throws IOException{
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

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mnI1, mnI2, mnI3, mnI4));


        // checking results wheter it is returning in proper order or not
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(musicians.get(0), musician2);
        assertEquals(musicians.get(1), musician1);

    }

    @DisplayName("for mostTalentedMusicians method whenever multiple musician has same number of instrument skill " +
            "should return those musician in any order")
    @Test
    public void shouldReturnMusicianInAnyOrderWhenMusiciansHasSameNumberOfInstrumentSkill()throws IOException {
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
    public void shouldReturnTheMusicianWhenThereIsOneOnly()throws IOException {
        Musician musician1 = new Musician("Keith Jarrett");
        List<Musician> list1 = Lists.newArrayList(musician1);

        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        album1.setFeaturedMusicians(list1);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
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

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));


        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(arr));
        assertEquals("number of most social musician to return should be more than 0", e.getMessage());
    }


    @DisplayName("Should return the most social musician in ordered manner in arrayList")
    @Test
    public void shouldReturnTheMusicianArrangedFromMostToLeastSocial()throws IOException {
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
        Album album2 = new Album(2020, "ECM 1998/2617", "RIVAGES");
        album2.setFeaturedMusicians(list2);
        Album album3 = new Album(2019, "ECM 1998/2645", "Characters on a Wall");
        album3.setFeaturedMusicians(list3);
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        album4.setFeaturedMusicians(list4);
        Album album5 = new Album(2020, "ECM 1998/2680", "Big Vicious");
        album5.setFeaturedMusicians(list5);
        Album album6 = new Album(2020, "ECM 1998/2659", "Promontire");
        album6.setFeaturedMusicians(list6);
        Album album7 = new Album(2017, "ECM 1998/2504", "Asian Field Variations");
        album7.setFeaturedMusicians(list7);
        Album album8 = new Album(2017, "ECM 1998/397030", "Bands Originals");
        album8.setFeaturedMusicians(list8);
        Album album9 = new Album(1999, "ECM 1998/17010", "Jean-Luc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1998/1668", "JOHANN HEINRICH SCHMELZER UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1998/1667", "FRANZ SCHUBERT KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1998/1591", "ARVO PART ALINA");
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
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
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
        Album album9 = new Album(1999, "ECM 1064/10", "JeaLuc Godard");
        album9.setFeaturedMusicians(list9);
        Album album10 = new Album(1999, "ECM 1064/1668", "JOHANN HEINRICH SCHMELZER UNARUM FIDIUM");
        album10.setFeaturedMusicians(list10);
        Album album11 = new Album(1999, "ECM 1064/1667", "FRANZ SCHUBERT KLAVIERSTUCKE");
        album11.setFeaturedMusicians(list11);
        Album album12 = new Album(1999, "ECM 1064/1591", "ARVO PART ALINA");
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
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
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
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
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
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
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
        Album album1 = new Album(1975, "ECM 1064/61", "The abcd Concert");
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
    public void returnsSimilarAlbumAccordingToMusician()throws IOException {
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

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("return 0 If No Album Similar With It")
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


        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.mostSimilarAlbums(3, album1);
        assertEquals(0,result.size());
    }

    /*
    ---------     Method 6 (mostExpensiveAlbums)  ----------
    ---------          Extra Credit 1            -----------
    */

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("most Expensive Price You Want should bigger than 0")
    public void mostExpensivePriceYouWantShouldBiggerThan0(int arr) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostExpensiveAlbums(arr));
        assertEquals("Expensive Price You Want should bigger than 0", e.getMessage());
    }

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

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
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

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
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


        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
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

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("number of highest rated album you want should bigger than zero")
    public void numberOfHighestRatedAlbumYouWantShouldBiggerThanZero(int arr) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.highestRatedAlbums(arr));
        assertEquals("Number of Highest rated albums you need should be more than zero", e.getMessage());
    }

    @Test
    @DisplayName("should return zero when no ratings inside database")
    public void shouldReturnZeroNumberOfAlbumsWhenNoAlbumRatingsAreThereInDatabase() {
        List<Album> result = ecmMiner.highestRatedAlbums(3);
        assertEquals(0,result.size());
    }

    @Test
    @DisplayName("Should return the  ratings when there are only two ratings are available")
    public void shouldReturnTheRatingsWhenOnlyTwoRatingsAreAvailable() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");

        album1.setRating(4.5);
        album3.setRating(3.0);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.highestRatedAlbums(5);

        assertEquals(2,result.size());
        assertTrue(result.contains(album1));
        assertTrue(result.contains(album3));
    }

    @Test
    @DisplayName("Should return the highest rated album")
    public void shouldReturnTheHighestRatedAlbum() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
        Album album2 = new Album(2020, "ECM 1064/2617", "RIVAGES");
        Album album3 = new Album(2019, "ECM 1064/2645", "Characters on a Wall");
        Album album4 = new Album(2007, "ECM 1998/99", "RE PASOLINI");
        Album album5 = new Album(2020, "ECM 1064/2680", "Big Vicious");
        Album album6 = new Album(2020, "ECM 1064/2659", "Promontire");

        album1.setRating(5.0);
        album3.setRating(4.5);
        album4.setRating(4);

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.highestRatedAlbums(1);

        assertEquals(1,result.size());
        assertTrue(result.contains(album1));
    }


    @Test
    @DisplayName("Should return the highest rated albums in ordered manner")
    public void shouldReturnHighestRatedAlbumsInProperOrder() {
        Album album1 = new Album(1976, "ECM 1064/61", "The abcd Concert");
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


        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> result = ecmMiner.highestRatedAlbums(6);
        assertEquals(5,result.size());


        assertEquals(result.get(0), album1);
        assertEquals(result.get(1), album2);
        assertEquals(result.get(2), album3);
        assertEquals(result.get(3), album4);
        assertEquals(result.get(4), album5);

    }

    //new

    @DisplayName("Start year must be smaller than end year")
    @ParameterizedTest
    @ValueSource(ints = {2005, 2010})
    public void prolificMusicianStartYearShouldSmallerThanEndYear(int arr)throws IOException {
        Album album1 = new Album(1975, "ECM 1064/61", "The Koln Concert");
        Musician musician1 = new Musician("Keith Jarrett");
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(1, arr,1999));
        assertEquals("Start year should smaller than end year", e.getMessage());
    }



    //new
    //test mostProlificMusicians
    @DisplayName("Start year must be smaller than end year")
    @ParameterizedTest
    @ValueSource(ints = {2005, 2010})
    public void startYearBiggerThanEndYear(int arg) throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> mostProlific = ecmMiner.mostProlificMusicians(2, 2005, arg);
        assertEquals(0, mostProlific.size());
    }

    @Test
    @DisplayName("if not album‘s release year between the input start year and endyear will return empty list")
    public void shouldReturnEmptyListWhenReleaseYearNotBetweenStartYearAndEndYear() throws MalformedURLException {
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician, musician1, musician2));
        List<Musician> mostProlific = ecmMiner.mostProlificMusicians(1, 1976, 2005);
        assertEquals(0, mostProlific.size());
    }



    @Test
    public void KValueBiggerThanListSizeForTalentedMusiciansWillReturnAllMusician() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        Musician musician1 = new Musician("Mia");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        MusicalInstrument musicalInstrument1 = new MusicalInstrument("Violin");
        MusicalInstrument musicalInstrument2 = new MusicalInstrument("Accordion");
        MusicianInstrument mi = new MusicianInstrument(musician, Sets.newHashSet(musicalInstrument,musicalInstrument1,musicalInstrument2 ));
        MusicianInstrument mi1 = new MusicianInstrument(musician1, Sets.newHashSet(musicalInstrument,musicalInstrument1));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mi1,mi));
        List<Musician> mostTalented = ecmMiner.mostTalentedMusicians(5);
        assertEquals(2, mostTalented.size());
    }


    @Test
    public void findTalentedMusicians() throws MalformedURLException {
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
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(mi1,mi,mi2));
        List<Musician> mostTalented = ecmMiner.mostTalentedMusicians(2);
        assertEquals(2, mostTalented.size());
        assertTrue(mostTalented.contains(musician) && mostTalented.contains(musician1));
    }

    @Test
    public void KValueBiggerThanListSizeForBusiesYears(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2003, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Integer> releasedYear = ecmMiner.busiestYears(10);
        assertEquals(3,releasedYear.size());
    }

    @Test
    public void KValueSmallerThanListSizeCanReturnTheBusiesYear(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2017, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Integer> releasedYear = ecmMiner.busiestYears(2);
        assertEquals(2,releasedYear.size());
        List<Integer> checkYear = Lists.newArrayList(2017,1975);
        assertEquals(checkYear,releasedYear);
    }

    @Test
    public void sameBusiestYearValueWillReturnOne(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2017, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        Album album4 = new Album(2017, "ECM 2347432", "The Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        List<Integer> releasedYear = ecmMiner.busiestYears(2);
        assertEquals(2,releasedYear.size());
        assertTrue(releasedYear.contains(1975)|| releasedYear.contains(2018));
    }



    @Test
    public void AlbumSizeBiggerThanKValueWillReturnTheSilimarAlbum() throws MalformedURLException {
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new  Musician("Keith Jarrett");
        Musician m1 = new Musician("Mia");
        Musician m2 = new Musician("Ben");
        List<Musician> musicians = Lists.newArrayList(musician,m1,m2);
        List<Musician> musicians1 = Lists.newArrayList(musician,m1,m2);
        List<Musician> musicians2 = Lists.newArrayList(musician);
        album1.setRating(3);
        album1.setStyle("Jazz");
        album1.setFeaturedMusicians(musicians);
        Album album2 = new Album(2003, "ECM 12344", "Great");
        album2.setRating(3);
        album2.setStyle("Jazz");
        album2.setFeaturedMusicians(musicians);
        Album album3 = new Album(2017, "ECM 13456", "Good");
        album3.setRating(4);
        album3.setStyle("Jazz");
        album3.setFeaturedMusicians(musicians1);
        Album album4 = new Album(2017, "ECM 13456", "Good");
        album4.setRating(1);
        album4.setStyle("Rock");
        album4.setFeaturedMusicians(musicians2);
        Album album5 = new Album(2000, "ECM 29474", "Book");
        album5.setRating(1);
        album5.setStyle("Rock");
        album5.setFeaturedMusicians(musicians2);
        Album album6 = new Album(2004, "ECM 123", "JAVA");
        album6.setRating(1);
        album6.setStyle("Rock");
        album6.setFeaturedMusicians(musicians2);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> albums = ecmMiner.mostSimilarAlbums(5,album1);
        assertEquals(4,albums.size());
    }


    @Test
    public void kValueSmallThanHighestRatingAlbumSize() throws MalformedURLException {
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new  Musician("Keith Jarrett");
        Musician m1 = new Musician("Mia");
        Musician m2 = new Musician("Ben");
        List<Musician> musicians = Lists.newArrayList(musician,m1,m2);
        List<Musician> musicians1 = Lists.newArrayList(musician,m1,m2);
        album1.setRating(3);
        album1.setStyle("Jazz");
        album1.setFeaturedMusicians(musicians);
        Album album2 = new Album(2003, "ECM 12344", "Great");
        album2.setRating(3);
        album2.setStyle("Jazz");
        album2.setFeaturedMusicians(musicians);
        Album album3 = new Album(2017, "ECM 13456", "Good");
        album3.setRating(4);
        album3.setStyle("Jazz");
        album3.setFeaturedMusicians(musicians1);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Album> albums = ecmMiner.highestRatingAlbum(5);
        assertEquals(3,albums.size());
    }

    @Test
    public void highestRatingAlbumSizeBiggerThanKValue() throws MalformedURLException {
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Musician musician = new  Musician("Keith Jarrett");
        Musician m1 = new Musician("Mia");
        Musician m2 = new Musician("Ben");
        List<Musician> musicians = Lists.newArrayList(musician,m1,m2);
        List<Musician> musicians1 = Lists.newArrayList(musician,m1);
        List<Musician> musicians2 = Lists.newArrayList(musician);
        album1.setRating(3);
        album1.setStyle("Jazz");
        album1.setFeaturedMusicians(musicians);
        Album album2 = new Album(2003, "ECM 12344", "Great");
        album2.setRating(3);
        album2.setStyle("Jazz");
        album2.setFeaturedMusicians(musicians);
        Album album3 = new Album(2017, "ECM 13456", "Good");
        album3.setRating(4);
        album3.setStyle("Jazz");
        album3.setFeaturedMusicians(musicians1);
        Album album4 = new Album(2010, "ECM 526474", "Python");
        album4.setRating(1);
        album4.setStyle("Rock");
        album4.setFeaturedMusicians(musicians2);
        Album album5 = new Album(2000, "ECM 29474", "Book");
        album5.setRating(1);
        album5.setStyle("Rock");
        album5.setFeaturedMusicians(musicians2);
        Album album6 = new Album(2004, "ECM 123", "JAVA");
        album6.setRating(1);
        album6.setStyle("Rock");
        album6.setFeaturedMusicians(musicians2);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5,album6));
        List<Album> albums = ecmMiner.highestRatingAlbum(3);
        assertEquals(3,albums.size());
        System.out.println(albums);
        assertTrue(albums.contains(album1)&&albums.contains(album2)&&albums.contains(album3));
    }

    @Test
    @DisplayName("Will return the correct number of best seller")
    public void shouldReturnTheCorrectNumberBestSeller()
    {
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2003, "ECM 12344", "Great");
        Album album3 = new Album(2017, "ECM 13456", "Good");
        album1.setSales(100);
        album2.setSales(10);
        album3.setSales(1);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Album> bestSellerAlbum= ecmMiner.bestSellerAlbum(2);
        assertEquals(2,bestSellerAlbum.size());
    }

    @Test
    @DisplayName("It should return only one value if there is only one album, even when K > 1 ")
    public void shouldReturnTheAlbumWhenThereIsOnlyOne()
    {
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        album1.setSales(100);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Album> bestSellerAlbum= ecmMiner.bestSellerAlbum(2);
        assertEquals(1,bestSellerAlbum.size());
    }




}
