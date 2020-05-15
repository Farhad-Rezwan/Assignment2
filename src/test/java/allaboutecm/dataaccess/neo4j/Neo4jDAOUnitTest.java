package allaboutecm.dataaccess.neo4j;

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
//        Configuration configuration = new Configuration.Builder().uri("http://neo4j:password@localhost:7474").build();

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

    @Test
    public void successfulCreationOfMusicianAndAlbum() throws IOException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
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

//        successful creation and loading of musical instrument
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


//-        successful creation and loading of album

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


//-        successful deletion of musician from musician and album
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


    // successfully delete musical instrument from only musical instrument class

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


    //successful loading and return of musician from musician class
    @Test
    public void successfulFindingOfMusicianName() throws Exception{
        Musician musician = new Musician("Keith Jarrett");

        dao.createOrUpdate(musician);
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertNotNull(loadedMusician.getId());
        assertEquals(musician, loadedMusician);

        assertEquals(1, dao.loadAll(Musician.class).size());

        Musician mus = dao.findMusicianByName(loadedMusician.getName());
        assertEquals(loadedMusician,mus);

    }

    /*successful loading and returning/reading of album in album class
        @Test
        public void successfulFindingAnAlbumWithAnythingAsInput() throws Exception{

            Album album = new Album(1975, "ECM 1064/66", "Tln Concert");

            dao.createOrUpdate(album);
            Collection<Album> albums = dao.loadAll(Album.class);
            assertEquals(1, albums.size());
            Album loadedAlbum = albums.iterator().next();
            assertEquals(album, loadedAlbum);
            assertEquals(album.getAlbumName(), loadedAlbum.getAlbumName());
            assertEquals(album.getReleaseYear(),loadedAlbum.getReleaseYear());
            assertEquals(album.getRecordNumber(), loadedAlbum.getRecordNumber());



            Album album1 = dao.findExistingEntity();



        }*/


    //


    }










