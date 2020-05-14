package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
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
}