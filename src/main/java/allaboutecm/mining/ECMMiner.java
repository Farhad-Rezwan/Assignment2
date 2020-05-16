package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.*;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * TODO: implement and test the methods in this class.
 * Note that you can extend the Neo4jDAO class to make implementing this class easier.
 */
public class ECMMiner {
    private static Logger logger = LoggerFactory.getLogger(ECMMiner.class);

    private final DAO dao;

    public ECMMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the most prolific musician in terms of number of albums released.
     *
     * @Param k the number of musicians to be returned.
     * @Param startYear, endYear between the two years [startYear, endYear].
     * When startYear/endYear is negative, that means startYear/endYear is ignored.
     */
    public List<Musician> mostProlificMusicians(int k, int startYear, int endYear) {
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        Map<String, Musician> nameMap = Maps.newHashMap();
        for (Musician m : musicians) {
            nameMap.put(m.getName(), m);
        }

        ListMultimap<String, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        for (Musician musician : musicians) {
            Set<Album> albums = musician.getAlbums();
            for (Album album : albums) {
                boolean toInclude =
                        !((startYear > 0 && album.getReleaseYear() < startYear) ||
                                (endYear > 0 && album.getReleaseYear() > endYear));

                if (toInclude) {
                    multimap.put(musician.getName(), album);
                }
            }
        }

/*
        musician1, musician1's album1
        musician1, musician1's album2
        musician2, musician2's album1
        musician2, musician2's album2
*/


        Map<String, Collection<Album>> albumMultimap = multimap.asMap();

/*
        musician1, musician1's album1, musician1's album2
        musician2, musician2's album1, musician2's album2
*/


        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums = albumMultimap.get(name);
            int size = albums.size();
            countMap.put(size, nameMap.get(name));
        }


/*
        (6, musician1)
        (9, musician2)
        (13, musician3)
        (13, musician4)
*/

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());

/*
        (13, musician4)
        (13, musician3)
        (9, musician2)
        (6, musician1)
*/

        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
//                  2           1, if the param k is one, than
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }

        return result;
    }

    /**
     * Most talented musicians by the number of different musical instruments they play
     *
     * @Param k the number of musicians to be returned.
     */
    public List<Musician> mostTalentedMusicians(int k) {

        Collection<MusicianInstrument> musicianInstrumentsCollection = dao.loadAll(MusicianInstrument.class);


        ListMultimap<String, MusicalInstrument> nameMusical = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        Map<String, Musician> musicianNameMap = Maps.newHashMap();

        for (MusicianInstrument m : musicianInstrumentsCollection) {
            musicianNameMap.put(m.getMusician().getName(), m.getMusician());
            for(MusicalInstrument mi : m.getMusicalInstruments())
            {
                nameMusical.put(m.getMusician().getName(),mi);
            }
        }
        //{musicianName,singleInstrument}
        Map<String, Collection<MusicalInstrument>> nameMusicalInstrument = nameMusical.asMap();

        for (String name : nameMusicalInstrument.keySet()) {
            Collection<MusicalInstrument> musicalInstruments = nameMusicalInstrument.get(name);
            int count = musicalInstruments.size();
            countMap.put(count, musicianNameMap.get(name));
        }

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());
        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
//                  2           1, if the param k is one, than
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }
        return result;
    }
















//    public List<Musician> mostTalentedMusicians(int k) {
//
//
//
//
//        Collection<MusicianInstrument> musicianInstrumentsCollection = dao.loadAll(MusicianInstrument.class);
//
//        ListMultimap<String, MusicianInstrument> nameMap = MultimapBuilder.treeKeys().arrayListValues().build();
//        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();
//
//        for (MusicianInstrument m : musicianInstrumentsCollection) {
//            nameMap.put(m.getMusician().getName(), m);
//        }
//
//
////        musician1, (musician1 musical instrument a, b)
////        musician1, (musician1 musical instrument a, b, c)
//
//
//        Map<String, Collection<MusicianInstrument>> mIMultimap = nameMap.asMap();
//
////        musician1,{(musician1 musical instrument a, b) , (musician1 musical instrument a, b, c)}
//        List<String> instrumentsAll = Lists.newArrayList();
//        List<String> instrumentsUnique = Lists.newArrayList();
//
//
//        for (String name : mIMultimap.keySet()) {
//            Collection<MusicianInstrument> musicianInstruments = mIMultimap.get(name);
//
//            Iterator<MusicianInstrument> itr = musicianInstruments.iterator();
//            while(itr.hasNext()) {
////              set {(musician1 musical instrument a, b)
//                Set<MusicalInstrument> mI;
//
//                mI = (itr.next().getMusicalInstruments());
//
//
//
//                for (Iterator<MusicalInstrument> it = mI.iterator(); it.hasNext(); ) {
//                    MusicalInstrument mIns = it.next();
//                    instrumentsAll.add(mIns.getName());
//                }
//
//                Set<String> s = new HashSet<String>();
//
//                for(String instrument : instrumentsAll) {
//                    if(s.add(instrument) == true){
//                        instrumentsUnique.add(instrument);
//                        s.add(instrument);
//                    }
//                }
//                int size = instrumentsUnique.size();
//                countMap.put(size, name);
//            }
//        }
///*
//        (6, musician1)
//        (9, musician2)
//        (13, musician3)
//        (13, musician4)
//
//*/
//        for(Integer i : countMap.keySet()){
//            String name = countMap.get(i);
//        }
//
//        List<Musician> result = Lists.newArrayList();
//        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
//        sortedKeys.sort(Ordering.natural().reverse());
//
///*
//        (13, musician4)
//        (13, musician3)
//        (9, musician2)
//        (6, musician1)
//
//*/
//
//        for (Integer count : sortedKeys) {
//            List<Musician> list = countMap.get(count);
////                  2           1, if the param k is one, than
//            if (list.size() >= k) {
//                int newAddition = k - result.size();
//                for (int i = 0; i < newAddition; i++) {
//                    result.add(list.get(i));
//                }
//                break;
//            }
//            if (result.size() + list.size() >= k) {
//                int newAddition = k - result.size();
//                for (int i = 0; i < newAddition; i++) {
//                    result.add(list.get(i));
//                }
//            } else {
//                result.addAll(list);
//            }
//        }
//
//        return result;
//
//    }



//    public List<Musician> mostTalentedMusicians(int k) {
//
//        Collection<MusicianInstrument> musicianInstrumentsCollection = dao.loadAll(MusicianInstrument.class);
//
//        ListMultimap<String, MusicianInstrument> nameMap = MultimapBuilder.treeKeys().arrayListValues().build();
//        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();
//
//        Map<String, Musician> musicianNameMap = Maps.newHashMap();
//
//        for (MusicianInstrument m : musicianInstrumentsCollection) {
//            nameMap.put(m.getMusician().getName(), m);
//            musicianNameMap.put(m.getMusician().getName(), m.getMusician());
//        }
//
//
////        musician1, (musician1 musical instrument a)
////        musician1, (musician1 musical instrument b)
//
//
//        Map<String, Collection<MusicianInstrument>> mIMultimap = nameMap.asMap();
//
////        musician1,{(musician1 musical instrument a, b) , (musician1 musical instrument a, b, c)}
//        //   musician2,{(musician1 musical instrument a, b)
//
//        for (String name : mIMultimap.keySet()) {
//            Collection<MusicianInstrument> musicianInstruments = mIMultimap.get(name);
//
//            ArrayList<Integer> newHashArray = new ArrayList<>();
//
//            Iterator<MusicianInstrument> itr = musicianInstruments.iterator();
//            while(itr.hasNext()) {
////              set {(musician1 musical instrument a, b)
//                Set<MusicalInstrument> mI;
//
//                mI = (itr.next().getMusicalInstruments());
//
//                ArrayList<String> instrumentsAll = new ArrayList<>();
//
//                ArrayList<String> instrumentsUnique = new ArrayList<>();
//
//                for (Iterator<MusicalInstrument> it = mI.iterator(); it.hasNext(); ) {
//                    MusicalInstrument mIns = it.next();
//                    instrumentsAll.add(mIns.getName());
//                }
//                //AllIns = m1 a, b
//                Set<String> s = new HashSet<>();
//                for(String instrument : instrumentsAll) {
//                    if(s.add(instrument) == true){
//                        instrumentsUnique.add(instrument);
//                    }
//                }
//                int size = instrumentsUnique.size();
//                if (!(itr.next().getMusician().hashCode() == 1)){
//                    countMap.put(size, musicianNameMap.get(name));
//                }
//
//                //{4,muscian4} {1,musician4}
//            }
//        }
//
////        Map<Integer, Musician> trueCountMap = countMap.asMap();
//        List<Musician> result = Lists.newArrayList();
//        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
//        sortedKeys.sort(Ordering.natural().reverse());
//        for (Integer count : sortedKeys) {
//            List<Musician> list = countMap.get(count);
////                  2           1, if the param k is one, than
//            if (list.size() >= k) {
//                int newAddition = k - result.size();
//                for (int i = 0; i < newAddition; i++) {
//                    result.add(list.get(i));
//                }
//                break;
//            }
//            if (result.size() + list.size() >= k) {
//                int newAddition = k - result.size();
//                for (int i = 0; i < newAddition; i++) {
//                    result.add(list.get(i));
//                }
//            } else {
//                result.addAll(list);
//            }
//        }
//        return result;
//    }







    /**
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */

    public List<Musician> mostSocialMusicians(int k) {
        return Lists.newArrayList();
    }

    /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    public List<Integer> busiestYears(int k) {
        if (k <= 0){
            throw new IllegalArgumentException("Busiest Years You Want should bigger than 0");
        }
        notNull(k);
        //get all albums from database
        Collection<Album> albums = dao.loadAll(Album.class);
        //Map(year,album)
        ListMultimap<Integer, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        //Get each year to reflect each album
        for (Album a : albums) {
            multimap.put(a.getReleaseYear(), a);
        }
        //transfer ListMultimap(Year,Album) to Map(Year, List<Album>)
        Map<Integer, Collection<Album>> yearAlbum = multimap.asMap();
        //build a ListMultimap(Count, Year) which can use as.Map() to transfer to Map(Count,list<Year>)
        ListMultimap<Integer, Integer> countMap = MultimapBuilder.treeKeys().arrayListValues().build();
        //Use for loop to divide different year and use size() to count how many album in each year
        for (Integer year : yearAlbum.keySet()) {
            Collection<Album> albums1 = yearAlbum.get(year);
            int count = albums1.size();
            countMap.put(count, year);
        }
        //build a empty arrayList to prepare to store result
        List<Integer> result = Lists.newArrayList();
        //Get all count number from countMap.count and sort them
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        //*****Sort it From big to small
        sortedKeys.sort(Ordering.natural().reverse());
        // From big count number to small, to add value in result
        for (Integer count : sortedKeys) {
            //Use current count of albums to get Year
            List<Integer> list = countMap.get(count);
            //if current number of year already bigger than we need, put Current Year to result and break loop
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            //if last number of year + current number of Year >= we need, put Current Year into result until it is full
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }


        return result;
    }


    /**
     * Most similar albums to a give album. The similarity can be defined in a variety of ways.
     * For example, it can be defined over the musicians in albums, the similarity between names
     * of the albums & tracks, etc.
     *
     * @Param k the number of albums to be returned.
     * @Param album
     */

    public List<Album> mostSimilarAlbums(int k, Album album) {
        if (k <= 0){
            throw new IllegalArgumentException("Similar Albums Number You Want should bigger than 0");
        }

        if (album == null){
            throw new NullPointerException("Album can not be null");
        }
        notNull(album);
        notNull(k);
        //Get all target musicians from the parameter Album as a list
        Collection<Musician> targetMusicians = album.getFeaturedMusicians();
        //Build a Map(Album, List<musicianName>)
        Map<Album, List<String>> AlbumMusician = Maps.newHashMap();
        //Get all albums from database that could be get musicians and compare with target musicians
        Collection<Album> albums = dao.loadAll(Album.class);
        //Choose each musician in target musicians
        for (Musician targetMusician : targetMusicians) {
            //Choose each album in all albums
            for (Album a : albums) {
                //delete the parameter album from the whole albums, add all musicians from current album into the list
                Collection<Musician> allMusicians = Lists.newArrayList();
                if(a != album){
                    allMusicians = a.getFeaturedMusicians();
                }
                //for each musician from current album's musicians, if the musician == current target musician
                //build a List that store current album's overlap musician
                //put Map(current Album, overlap musician Name List)
                List<String> musicianNames = Lists.newArrayList();
                for (Musician am : allMusicians) {
                    if (am == targetMusician) {
                        //List<String> musicianNames = Lists.newArrayList();
                        musicianNames.add(targetMusician.getName());
                        AlbumMusician.put(a, musicianNames);
                    }
                }
            }
        }
        //countMap<number of Overlap musicians, Album>
        ListMultimap<Integer, Album> countMap = MultimapBuilder.treeKeys().arrayListValues().build();
        //get the number of overlap musicians by size()
        for (Album a1 : AlbumMusician.keySet()) {
            List<String> musicians1 = AlbumMusician.get(a1);
            int count = musicians1.size();
            countMap.put(count, a1);
        }

        //build a empty arrayList to prepare to store result
        List<Album> result = Lists.newArrayList();
        //Get all count number from countMap.count and sort them
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        //*****Sort it From big to small
        sortedKeys.sort(Ordering.natural().reverse());
        // From big count number to small, to add value in result
        for (Integer count : sortedKeys) {
            //Use current count of overlap musicians to get Album
            List<Album> list = countMap.get(count);
            //if current number of albums already bigger than we need, break loop
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            //if last number of albums + current number of albums >= we need, add Album into result until it is full
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }

        return result;

    }
}
