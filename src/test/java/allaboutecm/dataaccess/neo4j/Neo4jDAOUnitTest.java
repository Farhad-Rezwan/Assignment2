package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO: add test cases to adequately test the Neo4jDAO class.
 */
class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";

    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void setUp() {
        // See @https://neo4j.com/docs/ogm-manual/current/reference/ for more information.

        // To use an impermanent embedded data store which will be deleted on shutdown of the JVM,
        // you just omit the URI attribute.

        // Impermanent embedded store
        Configuration configuration = new Configuration.Builder().build();

        // Disk-based embedded store
        // Configuration configuration = new Configuration.Builder().uri(new File(TEST_DB).toURI().toString()).build();

        // HTTP data store, need to install the Neo4j desktop app and create & run a database first.
        //Configuration configuration = new Configuration.Builder().uri("http://neo4j:password@localhost:7474").build();

        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(session);
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
        File testDir = new File(TEST_DB);
        if (testDir.exists()) {
//            FileUtils.deleteDirectory(testDir.toPath());
        }
    }

    @Test
    public void daoIsNotEmpty() {
        assertNotNull(dao);
    }


    // CRUD operation tests for Musician Class
    @DisplayName("successful Creation And Loading Of Musician")
    @Test
    public void successfulCreationAndLoadingOfMusician() throws IOException {
        assertEquals(0, dao.loadAll(Musician.class).size());

        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertNotNull(loadedMusician.getId());
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());

        assertEquals(1, dao.loadAll(Musician.class).size());

//        dao.delete(musician);
//        assertEquals(0, dao.loadAll(Musician.class).size());
    }


    //successful deletion of musician from musician and album

    @DisplayName("successful Delete Of Musician")
    @Test
    public void successfulDeleteOfMusician() throws Exception{
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertNotNull(loadedMusician.getId());
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());

        assertEquals(1, dao.loadAll(Musician.class).size());
        dao.delete(musician);
        assertEquals(0, dao.loadAll(Musician.class).size());

    }

    //**
    //successful loading and return of musician from musician class

    @DisplayName("successful Finding Of Musician Name")
    @Test
    public void successfulFindingOfMusicianName() throws Exception{

        Musician musician = new Musician("Keith Jarrett");
        Musician musician1 = new Musician("Cathy Jane");
        Musician musician2 = new Musician("Mark Paulo");

        dao.createOrUpdate(musician);
        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(3, musicians.size());

        Musician mus = dao.findMusicianByName("Cathy Jane");
        assertEquals(mus,musician1);


    }
    //**

    //successful updation of musician details in musician and album
    //name of the musician changes so we need an update in the system*******

    @DisplayName("successful Updation Of Musician  details")
    @Test
    public void successfulUpdationOfMusician() throws Exception{
//url time out
        Musician mus1 = new Musician("Katty Pery");
        mus1.setName("Katty Pery");
        mus1.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));

        dao.createOrUpdate(mus1);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());

        Musician loadedMusician = dao.load(Musician.class, mus1.getId());
        assertEquals(mus1, loadedMusician);

        loadedMusician.setName("Katy Perry");
        assertEquals(loadedMusician.getName(),"Katy Perry");
        assertEquals(loadedMusician.getMusicianUrl(),mus1.getMusicianUrl());

    }


    @DisplayName("successful Creation Of Musician And Album")
    @Test
    public void successfulCreationOfMusicianAndAlbum() throws IOException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The k Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());
    }


    //CRUD operation testing on Musical Instrument

    //successful creation and loading of musical instrument

    @DisplayName("successful Creation And Loading Of MusicalInstrument")
    @Test
    public void successfulCreationAndLoadingOfMusicalInstrument() throws IOException{
        MusicalInstrument musIn = new MusicalInstrument("Flute");
        dao.createOrUpdate(musIn);
        Collection<MusicalInstrument> musIns = dao.loadAll(MusicalInstrument.class);
        assertEquals(1, musIns.size());
        MusicalInstrument loadedMusicalInstrument = musIns.iterator().next();
        assertEquals(musIn, loadedMusicalInstrument);
        assertEquals(musIn.getName(), loadedMusicalInstrument.getName());

    }

    // successfully delete musical instrument from only musical instrument class


    @DisplayName("successful Delete Of MusicalInstrument")
    @Test
    public void successfulDeleteOfMusicalInstrument() throws Exception{

        MusicalInstrument musI = new MusicalInstrument("flute");
        dao.createOrUpdate(musI);

        MusicalInstrument loadedMusicalInstrument = dao.load(MusicalInstrument.class, musI.getId());
        assertNotNull(loadedMusicalInstrument.getId());
        assertEquals(musI, loadedMusicalInstrument);
        assertEquals(musI.getName(), loadedMusicalInstrument.getName());

        assertEquals(1, dao.loadAll(MusicalInstrument.class).size());
        dao.delete(musI);
        assertEquals(0, dao.loadAll(MusicalInstrument.class).size());
    }

    //CRUD operations on Musician Instrument class

    //creating and loading Musician Instrument in database


    @DisplayName("successful Creation And Loading MusicianInstrument")
    @Test
    public void successfulCreationAndLoadingMusicianInstrument() throws Exception{

        Musician mus = new Musician("Avishai Cohen");
        mus.setMusicianUrl(new URL("http://www.avishaicohenmusic.com/"));

        MusicalInstrument musIn = new MusicalInstrument("Trumpet");

        MusicianInstrument musIn1 = new MusicianInstrument(mus,Sets.newHashSet(musIn));
        dao.createOrUpdate(musIn1);

        Collection<MusicianInstrument> musIn1s = dao.loadAll(MusicianInstrument.class);
        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musIn1.getId());
        assertEquals(1,dao.loadAll(MusicalInstrument.class).size());
        assertEquals(musIn1, loadedMusicianInstrument);

    }

    // updating musical Instrument for Musician Instrument ***************


    @DisplayName("successful Updation Of Musical Instrument For Musician")
    @Test
    public void successfulUpdationOfMusicalInstrumentForMusician() throws Exception{

        Musician mus = new Musician("Courtois Hugiwin");
        MusicalInstrument mi = new MusicalInstrument("Piano");
        MusicalInstrument mi1 = new MusicalInstrument("Guitar");

        MusicianInstrument musicianIns = new MusicianInstrument(mus,Sets.newHashSet(mi));

        dao.createOrUpdate(musicianIns);
        Collection<MusicianInstrument> musIns = dao.loadAll(MusicianInstrument.class);
        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musicianIns.getId());
        assertEquals(1, dao.loadAll(MusicianInstrument.class).size());
        assertEquals(loadedMusicianInstrument,musicianIns);

        loadedMusicianInstrument.setMusicalInstruments(Sets.newHashSet(mi1));

        assertEquals(loadedMusicianInstrument.getMusicalInstruments().iterator().next().getName(),mi1.getName());


    }

    // deleting Musician Instrument object

    @DisplayName("successful Deleting Of Musical Instrument For Musician")
    @Test
    public void successfulDeletionOfMusicianInstrument() throws Exception{

        Musician mus = new Musician("Avishai Cohen");
        mus.setMusicianUrl(new URL("http://www.avishaicohenmusic.com/"));

        MusicalInstrument musIn = new MusicalInstrument("Trumpet");

        MusicianInstrument musIn1 = new MusicianInstrument(mus,Sets.newHashSet(musIn));
        dao.createOrUpdate(musIn1);

        Collection<MusicianInstrument> musIn1s = dao.loadAll(MusicianInstrument.class);
        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musIn1.getId());
        assertEquals(1,dao.loadAll(MusicalInstrument.class).size());
        assertEquals(musIn1, loadedMusicianInstrument);

        dao.delete(musIn1);
        assertEquals(0,dao.loadAll(MusicianInstrument.class).size());

    }


    //CRUD operation testing for Album Class

    //successful creation and loading of album


    @DisplayName("successful Creation And Loading Of Album")
    @Test
    public void successfulCreationAndLoadingOfAlbum() throws IOException{
        Album album = new Album(1975, "ECM 1064/66", "Tln Concert");
        dao.createOrUpdate(album);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getAlbumName(), loadedAlbum.getAlbumName());
        assertEquals(album.getReleaseYear(),loadedAlbum.getReleaseYear());
        assertEquals(album.getRecordNumber(), loadedAlbum.getRecordNumber());


    }

//-        successful deletion of album only(musician related to album will stay in db)

    @DisplayName("successful Delete Of Album")
    @Test
    public void successfulDeleteOfAlbum() throws IOException{
        Album album = new Album(1975, "ECM 1064/66", "Tln Concert");
        dao.createOrUpdate(album);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getAlbumName(), loadedAlbum.getAlbumName());
        assertEquals(album.getReleaseYear(),loadedAlbum.getReleaseYear());
        assertEquals(album.getRecordNumber(), loadedAlbum.getRecordNumber());
        dao.delete(album);

    }


    //successful updation of Album

    @DisplayName("Successful Updation Of Album")
    @Test
    public void successfulUpdationOfAlbum() throws Exception{

        Album album = new Album(1975, "ECM 1064/66", "Tln Concert");

        Musician mus1 = new Musician("Katty Pery");
        mus1.setName("Katty Pery");
        mus1.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));

        dao.createOrUpdate(mus1);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());

        Musician loadedMusician = dao.load(Musician.class, mus1.getId());
        assertEquals(mus1, loadedMusician);

        loadedMusician.setName("Katy Perry");
        assertEquals(loadedMusician.getName(),"Katy Perry");
        assertEquals(loadedMusician.getMusicianUrl(),mus1.getMusicianUrl());

    }

    //new
    @Test
    public void deleteMusicianButAlbumCannotBeExisted()throws IOException {
        Musician m1 = new Musician("Mia");
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        dao.createOrUpdate(m1);
        dao.createOrUpdate(album1);
        m1.setAlbums(Sets.newHashSet(album1));
        dao.deleteAlbumInMusician(m1);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(0,musicians.size());
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(0,albums.size());
    }

    @Test
    public void deleteMusicianCanOnlyDeleteHisOwnAlbumSet()throws IOException{
        Musician m1 = new Musician("Mia");
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        Album album2 = new Album(2008, "ECM 1998/72", "Beauty");
        dao.createOrUpdate(m1);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        m1.setAlbums(Sets.newHashSet(album1));
        dao.deleteAlbumInMusician(m1);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(0,musicians.size());
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1,albums.size());
    }

    @Test
    public void deleteAlbumButFeaturedMusiciansStillExisting()throws IOException {
        Musician m1 = new Musician("Mia");
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        album1.setFeaturedMusicians(Lists.newArrayList(m1));
        dao.createOrUpdate(m1);
        dao.createOrUpdate(album1);
        dao.delete(album1);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(0,albums.size());
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1,musicians.size());
    }

    @Test
    public void deleteMusicianAndMusicianInstrumentCanBeDelete() throws IOException{
        Musician m1 = new Musician("Mia");
        Musician m2 = new Musician("Ben");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        MusicianInstrument musicianInstrument = new MusicianInstrument(m1,Sets.newHashSet(musicalInstrument));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(m2,Sets.newHashSet(musicalInstrument));
        dao.createOrUpdate(m1);
        dao.createOrUpdate(m2);
        dao.createOrUpdate(musicianInstrument);
        dao.createOrUpdate(musicianInstrument2);
        dao.deleteMusicianAndMusicianInstrumentAlsoBeDeleted(m1);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1,musicians.size());
        Collection<MusicianInstrument> musicianInstruments = dao.loadAll(MusicianInstrument.class);
        assertEquals(1,musicianInstruments.size());
    }


    //new--some errors as the site used an example did not confirm that if its valid
    @Test
    public void cannotSaveTheSameMusicianTwice() throws IOException {
        Musician musician1 = new Musician("Katy Perry");
        musician1.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));
        dao.createOrUpdate(musician1);

        Musician musician2 = new Musician("Katy Perry");
        musician2.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));
        dao.createOrUpdate(musician2);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());
    }


    //new
    @Test
    public void sameAlbumNameAndRecordNumberAndAlbumNameCannotBeSavedAsTwice(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        dao.createOrUpdate(album1);
        Album album2 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        dao.createOrUpdate(album2);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
    }

    @Test
    public void sameAlbumNameAndSameRecordNumberWithDifferentReleaseYearCanBeSavedAsTwoNode(){
        Album album1 = new Album(1975, "ECM 1064/65", "The Koln Concert");
        dao.createOrUpdate(album1);
        Album album2 = new Album(2008, "ECM 1064/65", "The Koln Concert");
        dao.createOrUpdate(album2);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(2, albums.size());
    }

    @Test
    public void sameMusicianInstrumentCannotBeSavedTwice() throws IOException {
        Musician musician1 = new Musician("Katy Perry");
        musician1.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));
        dao.createOrUpdate(musician1);
        MusicalInstrument mi1 = new MusicalInstrument("Piano");
        dao.createOrUpdate(mi1);
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1,Sets.newHashSet(mi1));
        dao.createOrUpdate(musicianInstrument1);

        Musician musician2 = new Musician("Katy Perry");
        musician2.setMusicianUrl(new URL("https://en.wikipedia.org/wiki/Katy_Perry"));
        dao.createOrUpdate(musician2);
        MusicalInstrument mi2 = new MusicalInstrument("Piano");
        dao.createOrUpdate(mi2);
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2,Sets.newHashSet(mi2));
        Collection< MusicianInstrument>  musicianInstruments = dao.loadAll(MusicianInstrument.class);
        dao.createOrUpdate(musicianInstrument2);
        assertEquals(1, musicianInstruments .size());
        assertEquals(musicianInstrument1.getMusician(),musicianInstruments.iterator().next().getMusician());
        assertEquals(musicianInstrument1.getMusicalInstruments(),musicianInstruments.iterator().next().getMusicalInstruments());
    }

    //new
    @Test
    public void afterSaveMusicianInstrumentMusicianCanBeUpdate() throws MalformedURLException {
        Musician m1 = new Musician("Mia");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        MusicianInstrument musicianInstrument = new MusicianInstrument(m1,Sets.newHashSet(musicalInstrument));
        dao.createOrUpdate(m1);
        dao.createOrUpdate(musicianInstrument);
        dao.createOrUpdate(musicalInstrument);
        m1.setName("Mia Li");
        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musicianInstrument.getId());
        assertEquals(m1.getName(),loadedMusicianInstrument.getMusician().getName());
    }



}










