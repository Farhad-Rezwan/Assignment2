package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.*;

import java.util.*;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Implement and test the methods in this class.
 * Note that you can extend the Neo4jDAO class to make implementing this class easier.
 */
public class ECMMiner {

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
       if (k <= 0){
        throw new IllegalArgumentException("number of most prolific musician to return should be more than 0");
       }
        int year = Calendar.getInstance().get(Calendar.YEAR);

        // if start year is negative then it is ignored
        if ((startYear > 0 || endYear > 0)){
            if(!((startYear>1970) && startYear<= year) || !((endYear>1970) && endYear<= year))
                throw new IllegalArgumentException("Years should be greater than 1970, not future, and valid year");
            if (startYear>endYear){
                throw new IllegalArgumentException("Start year should smaller than end year");
            }
        }





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
        Map<String, Collection<Album>> albumMultimap = multimap.asMap();

        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums = albumMultimap.get(name);
            int size = albums.size();
            countMap.put(size, nameMap.get(name));
        }

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());


        // this for loop is used frequently in this class, this makes sure with parameter K, is returning proper value
        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
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
        if (k <= 0){
            throw new IllegalArgumentException("number of most talented musician to return should be more than 0");
        }

        // Loading all the all the MusicianInstruments objects in Collection.
        Collection<MusicianInstrument> musicianInstrumentsCollection = dao.loadAll(MusicianInstrument.class);

        // nameMusical = stores musicalInstrument name and MusicalInstrument's object as multimap.
        ListMultimap<String, MusicalInstrument> nameMusical = MultimapBuilder.treeKeys().arrayListValues().build();
        // stores musician's number of instrument played and musician object as multimap.
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        Map<String, Musician> musicianNameMap = Maps.newHashMap();


        // loops through all the musicianInstruments and fills musicianName map and nameMusical multimap.
        for (MusicianInstrument m : musicianInstrumentsCollection) {
            musicianNameMap.put(m.getMusician().getName(), m.getMusician());
            for(MusicalInstrument mi : m.getMusicalInstruments())
            {
                nameMusical.put(m.getMusician().getName(),mi);
            }
        }

        //converts multimap to map nameMusicalInstrument, keeping the musicianName and collection of single instruments
        Map<String, Collection<MusicalInstrument>> nameMusicalInstrument = nameMusical.asMap();


        // loops through all the nameMusicalInstruments by key, which is name of the musician, to get counts of his number of
        // instruments involvement.
        for (String name : nameMusicalInstrument.keySet()) {

            Set<MusicalInstrument> allUniqueInstruments = new HashSet<>();

            Collection<MusicalInstrument> musicalInstruments = nameMusicalInstrument.get(name);

            // to make sure that all unique musical instruments are counted, not duplicates
            for (MusicalInstrument m : musicalInstruments) {
                if(!allUniqueInstruments.contains(m))
                {
                    allUniqueInstruments.add(m);
                }
            }

            countMap.put(allUniqueInstruments.size(), musicianNameMap.get(name));
        }

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());

        // this for loop is used frequently in this class, this makes sure parameter K is returning proper value
        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
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
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */
    public List<Musician> mostSocialMusicians(int k) {
        if (k <= 0){
            throw new IllegalArgumentException("number of most social musician to return should be more than 0");
        }

        Collection<Album> albumCollection = dao.loadAll(Album.class);
        Map<String, Musician> nameMap = Maps.newHashMap();

        for (Album m : albumCollection) {
            for (Musician musician : m.getFeaturedMusicians()) {
                nameMap.put(musician.getName(), musician);
            }
        }

        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();


        /* to loop through all the individual musicians, and fills countMap, a MultiMap which stores number of other
                  musicians they worked with and the Musician Object.
        */
        for (String singleMusicianName : nameMap.keySet()) {
            Set<Musician> teamMateMusicians = new HashSet<>();

            // loops through all the albums, gets each album's musicians
            for (Album singleAlbum : albumCollection) {
                List<Musician> CurrentAlbumMusicians = singleAlbum.getFeaturedMusicians();
                if (CurrentAlbumMusicians.contains(nameMap.get(singleMusicianName))) {
                    List<Musician> albumOtherMusicians = Lists.newArrayList();
                    for(Musician a : CurrentAlbumMusicians){

                        /* inserts other musicians into the list, make sure current musician(for whom we are counting the
                         *     number of musician they worked with) is not inserted
                         * */
                        if(a != nameMap.get(singleMusicianName)){
                            albumOtherMusicians.add(a);
                        }
                    }

                    for (Musician m : albumOtherMusicians) {
                        if(!teamMateMusicians.contains(m))
                        {
                            teamMateMusicians.add(m);
                        }
                    }
                }
            }
            countMap.put(teamMateMusicians.size(), nameMap.get(singleMusicianName));
        }



        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());

        sortedKeys.sort(Ordering.natural().reverse());

        for (Integer count : sortedKeys) {
            // use current count to get a musician
            List<Musician> list = countMap.get(count);
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

    /**
     * Extra Credit 1:
     * Most Expensive Albums
     *
     * @Param k the number of Albums to be returned.
     */
    public List<Album> mostExpensiveAlbums(int k) {
        if (k <= 0){
            throw new IllegalArgumentException("Expensive Price You Want should bigger than 0");
        }
        notNull(k);
        //get all albums from database
        Collection<Album> albums = dao.loadAll(Album.class);
        //Map(price,album)
        ListMultimap<Double, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        //Get each price to reflect each album
        for (Album a : albums) {
            if(a.getPrice() != null) {
                multimap.put(a.getPrice(), a);
            }
        }

        //build a empty arrayList to prepare to store result
        List<Album> result = Lists.newArrayList();
        //Get all price from multimap.count and sort them
        List<Double> sortedKeys = Lists.newArrayList(multimap.keySet());
        //*****Sort it From big to small
        sortedKeys.sort(Ordering.natural().reverse());
        // From big price number to small, to add value in result
        for (Double price : sortedKeys) {
            //Use current price to get albums
            List<Album> list = multimap.get(price);
            //if current price's number of album already bigger than we need, put them to result and break loop
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            //if last price's number of albums + current price albums >= we need, put them into result until it is full
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
     * Extra Credit 2:
     * Highest Rated Albums
     *
     * @Param k the number of Albums to be returned.
     */
    public List<Album> highestRatedAlbums(int k) {
        if (k <= 0){
            throw new IllegalArgumentException("Number of Highest rated albums you need should be more than zero");
        }
        notNull(k);

        //get all albums from database
        Collection<Album> albums = dao.loadAll(Album.class);

        //Map(ratings,album)
        ListMultimap<Double, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();

        //Get each ratings to reflect each album
        for (Album a : albums) {
            if(a.getRating() != null)
                multimap.put(a.getRating(), a);
        }

        //build a empty arrayList to prepare to store result
        List<Album> result = Lists.newArrayList();

        //Get all ratings from multimap.count and sort them
        List<Double> sortedKeys = Lists.newArrayList(multimap.keySet());

        // sorting from highest rating to the lowest rating
        sortedKeys.sort(Ordering.natural().reverse());

        // From highest rating to lowest rating, to add value in result

        for (Double ratings : sortedKeys) {

            //Use current ratings to get albums
            List<Album> list = multimap.get(ratings);

            //if current ratings's number of album already bigger than we need, put them to result and break loop
            if (list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
                break;
            }
            //if last ratings's number of albums + current ratings albums >= we need, put them into result until it is full
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
     * Finding best sellers
     * Extra part
     * @Param k the number of albums to be returned.
     */
    public List<Album> bestSellerAlbum(int k) {
        if (k <=0) {
            return Lists.newArrayList();
        }
        Collection<Album> albums = dao.loadAll(Album.class);
        ListMultimap<Integer,Album> albumSales = MultimapBuilder.treeKeys().arrayListValues().build();
        for(Album a: albums){
            albumSales.put(a.getSales(),a);
        }

        List<Album> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(albumSales.keySet());
        sortedKeys.sort(Ordering.natural().reverse());

        for(Integer count : sortedKeys){
            List<Album> list = albumSales.get(count);
            if (list.size() >= k) {
                break;
            }
            if(result.size() + list.size() >= k){
                int addition = k - result.size();
                for(int i=0; i < addition; i++){
                    result.add(list.get(i));
                }
            } else{
                result.addAll(list);
            }
        }
        return result;
    }

    public List<Album> highestRatingAlbum(int k){
        if (k <=0) {
            return Lists.newArrayList();
        }
        Collection<Album> albums = dao.loadAll(Album.class);
        ListMultimap<Double,Album> albumRating = MultimapBuilder.treeKeys().arrayListValues().build();
        for(Album a: albums){
            albumRating.put(a.getRating(),a);
        }

        List<Album> result = Lists.newArrayList();
        List<Double> sortedKeys = Lists.newArrayList(albumRating.keySet());
        sortedKeys.sort(Ordering.natural().reverse());

        for(Double count : sortedKeys){
            List<Album> list = albumRating.get(count);
            if (list.size() >= k) {
                break;
            }
            if(result.size() + list.size() >= k){
                int addition = k - result.size();
                for(int i=0; i < addition; i++){
                    result.add(list.get(i));
                }
            } else{
                result.addAll(list);
            }
        }
        return result;
    }




}
