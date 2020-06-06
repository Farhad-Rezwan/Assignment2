package allaboutecm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumUnitTest {
    private Album album;

    @BeforeEach
    public void setUp() {
        //The Köln Concert
        album = new Album(1975, "ECM 1064/65", "The abcd Concert");
    }


    @Test
    @DisplayName("Album name cannot be null")
    public void albumNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setAlbumName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Album name cannot be empty or blank")
    public void albumNameCanNotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
    }

    @DisplayName("Same name and number means same album")
    @Test
    public void sameNameAndNumberMeansSameAlbum() {
        Album album1 = new Album(1975, "ECM 1064/65", "The abcd Concert");

        assertEquals(album, album1);
    }

    //    ------------------------------------
    @DisplayName("Record number should return proper value while adding and updating")
    @Test
    public void recordNumberShouldReturnProperValueAddingAndUpdating() {
        Album album2 = new Album(2019, "ECM 1064/2680", "BIG VICIOUS");
        assertTrue("ECM 1064/2680".equals(album2.getRecordNumber()));
        album2.setRecordNumber("ECM 2222/2680");
        assertTrue("ECM 2222/2680".equals(album2.getRecordNumber()));
    }

    @DisplayName("Record number with null argument should throw NullPointerException")
    @Test
    public void shouldThrowExceptionWhenRecordNumberSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> album.setRecordNumber(null));
        assertEquals("Record Number can not be null", e.getMessage());
    }

    @DisplayName("Record Number can only accept Alphanumeric, and should not accept special characters")
    @ParameterizedTest
    @ValueSource(strings = {"*", "&", "%"})
    public void recordNumberCanOnlyAcceptAlphanumericWithSpaceORWithForwardSlash(String args){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> album.setRecordNumber(args));
        assertEquals("Illegal record number", e.getMessage());
    }

    @DisplayName("Record number should only accept " +
            "predefined prefixes ie. ECM, Carmo, RJAL, " +
            "YAN, Watt, and XtraWatt, otherwise throw illegal argument exception")
    @ParameterizedTest
    @ValueSource(strings = {"ECM1211", "IDONTKNO 1212"})
    public void recordNumberShouldOnlyAcceptPredefinedPrefixWithSpace(String args) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->album.setRecordNumber(args));
        assertEquals("Illegal record number", e.getMessage());
    }

    @DisplayName("RecordNumber prefix is case sensitive")
    @Test
    public void shouldThrowIllegalArgumentExceptionPrefixCaseIsNotFollowed() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->album.setRecordNumber("ecm 1212"));
        assertEquals("Illegal record number", e.getMessage());
    }


    @DisplayName("Record Number can only accept suffix of number, so IllegalArgumentException is thrown")
    @ParameterizedTest
    @ValueSource(strings = {"XtraWatt 1*12", "ECM XYZA"})
    public void recordNumberShouldOnlyAcceptSuffixOfNumber(String args) {
        assertThrows(IllegalArgumentException.class, () ->album.setRecordNumber(args));
    }

    @DisplayName("Record Number can only accept suffix of a number which might contain forward-slash like  \"ECM 1064/65\"")
    @ParameterizedTest
    @ValueSource(strings = {"ECM 1064/65", "XtraWatt 12/223"})
    public void shouldAcceptProperRecordNumber(String goodRecordNumbers) {
        album.setRecordNumber(goodRecordNumbers);
        assertTrue(goodRecordNumbers == album.getRecordNumber());
    }

    @DisplayName("Should throw IllegalArgumentException when record number does not have space after the prefixes")
    @Test
    public void test() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->album.setRecordNumber("XtraWatt12"));
        assertTrue("Illegal record number" == e.getMessage());
    }

    @DisplayName("Should throw NullPointerException when featured musician is set to null")
    @Test
    public void shouldThrowExceptionWhenFeaturedMusicianSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> album.setFeaturedMusicians(null));
        assertEquals("Featured musician list cannot be null", e.getMessage());
    }

    @DisplayName("Same name for two musician should refer same Musician object.")
    @Test
    public void twoMusicianNamesShouldReferSameMusician()throws IOException {
        Musician m = new Musician("Farhad Ullah Rezwan");
        List<Musician> lists = new ArrayList<>();
        lists.add(m);
        album.setFeaturedMusicians(lists);
        assertEquals(album.getFeaturedMusicians(), lists);
    }

    @DisplayName("Two Musican Instrument should refer to same musician and musician instrument")
    @Test
    public void twoMusicalInstrumentShouldReferSameMusicianAndSameMusicalInstrumentOfMusicianInstrumentAttribute()throws IOException {
        Musician m = new Musician("Farhad Ullah Rezwan");
        MusicalInstrument i = new MusicalInstrument("Violin");
        MusicianInstrument mi = new MusicianInstrument(m, Sets.newHashSet(i));

        Set<MusicianInstrument> s = new HashSet<>();
        s.add(mi);
        album.setInstruments(s);

        for (Iterator<MusicianInstrument> musicianInstruments = s.iterator(); musicianInstruments.hasNext();) {
            MusicianInstrument f = musicianInstruments.next();
            assertTrue(f.equals(new MusicianInstrument(new Musician("Farhad Ullah Rezwan"), Sets.newHashSet(new MusicalInstrument("Violin")))));
        }
    }

    @DisplayName("Should throw NullPointerException when instruments is set to null")
    @Test
    public void shouldThrowExceptionWhenSetInstrumentsSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> album.setInstruments(null));
        assertEquals("Instruments list cannot be null", e.getMessage());
    }


    @DisplayName("Should Throw Unknown Host Exception when invalid URL is set")
    @Test
    public void shouldThrowUnknownHostExceptionWhenInvalidURLIsSet() {
        assertThrows(UnknownHostException.class, () -> album
                .setAlbumURL(new URL("https://www.goasdfasdfasdfaogle.com")));
    }

    @DisplayName("should throw illegal argument exception when hostname does not contains \"ecmrecords\"")
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenHostnameDoesNotContainEcmecords() {
        assertThrows(IllegalArgumentException.class, () ->album
                .setAlbumURL(new URL("https://soundcloud.com/roddyricch/the-box")));
    }

    @Test
    @DisplayName("Null pointer exception should be thrown if Album URL is set null")
    public void shouldThrowExceptionWhenAlbumURLSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> album.setAlbumURL(null));
        assertEquals("Album URL cannot be null", e.getMessage());
    }

    @DisplayName("Should return proper albumURL when set from ECM website.")
    @Test
    public void shouldReturnProperECMURL() throws IOException {
        URL u = new URL("https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett");
        album.setAlbumURL(u);
        assertEquals(album.getAlbumURL(),u);
    }

    @DisplayName("Should throw null pointer exception when tracks is set to null")
    @Test
    public void shouldThrowNullPointerExceptionWhenTrackSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, ()-> album.setTracks(null));
        assertEquals("Tracks list cannot be null", e.getMessage());
    }

    @DisplayName("Same track name should refer to the same Track object")
    @Test
    public void twoTracksWithSameNameAndLengthShouldReferSameTrack() {
        String t = "HONEY FOUNTAIN";
        List s = new ArrayList();
        s.add(t);
        album.setTracks(s);

        assertEquals(album.getTracks(), s);

    }

    // to provide value for Parameterized Test shouldRejectEmptyOrBlankTrackName
    static Stream<Arguments> generateData1() {
        return Stream.of(
                Arguments.of(Arrays.asList("", " ", "    \t"))
        );
    }

    @DisplayName("Should reject empty or blank track names")
    @ParameterizedTest
    @MethodSource("generateData1")
    public void shouldRejectEmptyOrBlankTrackNames(List<String> emptyOrBlank) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->album.setTracks(emptyOrBlank));
        assertEquals("Not a valid track name", e.getMessage());
    }


    // to provide value for Parameterized Test shouldRejectImproperAlbumNameWithOneOrMultipleLetters
    static Stream<Arguments> generateData2() {
        return Stream.of(
                Arguments.of(Arrays.asList("@", "$", "_")),
                Arguments.of(Arrays.asList("   F", "F   ", "f12")),
                Arguments.of(Arrays.asList("1212", "0000"))
        );
    }

    @DisplayName("Should reject improper track name with one or multiple letters")
    @ParameterizedTest
    @MethodSource("generateData2")
    public void shouldRejectImproperAlbumNameWithOneOrMultipleLetters(List<String> oneOrMultipleInvalidLatters) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->album.setTracks(oneOrMultipleInvalidLatters));
        assertEquals("Not a valid track name", e.getMessage());
    }


    // to provide value for Parameterized Test shouldAcceptProperTrackNamesLists
    static Stream<Arguments> generateData3() {
        return Stream.of(
                Arguments.of(Arrays.asList("HONEY FOUNTAIN", "HIDDEN CHAMBER", "King Kunter")),
                Arguments.of(Arrays.asList("El-pardo'n", "Farhad's November Rain"))
        );
    }

    @DisplayName("Should accept proper track name's lists")
    @ParameterizedTest
    @MethodSource("generateData3")
    public void shouldAcceptProperTrackNamesLists(List<String> properTrackNames) {
        album.setTracks(properTrackNames);
        assertEquals(album.getTracks(), properTrackNames);
    }

    @DisplayName("Should throw null pointer exception when rating is set to null")
    @Test
    public void shouldThrowNullPointerExceptionWhenRatingIsSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, ()-> album.setRating(null));
        assertEquals("Rating value should not be null", e.getMessage() );
    }

    @DisplayName("Should throw illegal argument exception when rating is set to negative double value")
    @ParameterizedTest()
    @ValueSource(doubles = {-2.5, -3.5})
    public void shouldThrowIllegalArgumentExceptionWhenRatingIsSetToNegative(Double rating) {
        assertThrows(IllegalArgumentException.class, () -> album.setRating(rating));
    }

    @DisplayName("Should throw illegal argument exception when rating is set to less than 0.00 or more than 5.00")
    @ParameterizedTest()
    @ValueSource(doubles = {-0.1, 5.1})
    public void shouldThrowIllegalArgumentExceptionWhenRatingValueSetOutOfRange(Double rating) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> album.setRating(rating));
        assertEquals("Rating should hold valid range", e.getMessage() );
    }

    @DisplayName("Should accept parameter of integers when required")
    @ParameterizedTest()
    @ValueSource(ints = {0, 1, 4, 5})
    public void shouldAcceptIntegerOfWholeNumber (int args) {
        album.setRating(args);
        assertTrue(album.getRating() == args);
    }
    @DisplayName("Should throw null pointer exception when price is set to null")
    @Test
    public void shouldThrowNullPointerExceptionWhenPriceIsSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, ()-> album.setPrice(null));
        assertEquals("price value should not be null", e.getMessage() );
    }

    @DisplayName("Should throw illegal argument exception when price is set to negative double value")
    @ParameterizedTest()
    @ValueSource(doubles = {-2.5, -3.5})
    public void shouldThrowIllegalArgumentExceptionWhenPriceIsSetToNegative(Double price) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> album.setPrice(price));
        assertEquals("Price should hold non negative numbers", e.getMessage() );
    }

    @DisplayName("Should throw illegal argument exception when price is set to negative int values")
    @ParameterizedTest()
    @ValueSource(ints = {-1, -100})
    public void shouldThrowIllegalArgumentExceptionWhenPriceIsSetOutOfRange(Integer price) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> album.setPrice(price));
        assertEquals("Price should hold non negative numbers", e.getMessage() );
    }

    @DisplayName("Should accept parameter of integers when required")
    @ParameterizedTest()
    @ValueSource(ints = {0, 11, 412, 500})
    public void priceShouldAcceptIntegerOfWholeNumber (int args) {
        album.setPrice(args);
        assertTrue(album.getPrice() == args);
    }
    @Test
    @DisplayName("Input should be a number(year)between 1970 and current.")
    public void releaseYearShouldBeBetween1970AndCurrent() {
        assertThrows(IllegalArgumentException.class, () -> album.setReleaseYear(2021));
    }

    @Test
    @DisplayName("to check if the set value is correctly returned")
    public void shouldReturnCorrectValueWhichIsSetForReleaseYear()
    {
        assertEquals(1975, album.getReleaseYear());
    }


    @DisplayName("Should throw exceptions when pass a null into album name to setAlbumName function")
    @Test
    public void shouldThrowExceptionWhenAlbumNameSetToNull() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> album.setAlbumName(null));
        assertEquals("album name cannot be null or empty", e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Album name cannot be empty or blank")
    public void albumNameCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
    }

    @DisplayName("Should reject improper album name with one or multiple letters")
    @ParameterizedTest
    @ValueSource(strings = {"1212", "@", "$", "_", "   F", "F   ", "f12"})
    public void shouldThrowIllegalArgumentExceptionWhenAlbumNameIsSetALetter(String args) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(args));
    }

    @DisplayName("Should accept proper album name")
    @ParameterizedTest
    @ValueSource(strings = {"LA MISTERIOSA MUSICA DELLA REGINA LOANA", "CONTE DE L'INCROYABLE AMOUR", "Oded Tzur", "Mal Waldron Trio"})
    public void shouldAcceptProperAlbumName(String args) {
        album.setAlbumName(args);
        assertTrue(args == (album.getAlbumName()));
    }

    //new
    @DisplayName("When Initialize illegal AlbumName, it should Throw Exception ")
    @Test
    public void shouldThrowExceptionWhenInitializeAlbumName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Album(2020, "ECM 1064/65", "Mal Köln Trio"));
        assertEquals("Not a valid album name", e.getMessage());
    }



    @Test
    public void lengthOfAlbumNameLessThan20(){
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName("asdfghjkjhgfdsaw34rtyuijnbvcxz"));
        assertThrows(IllegalArgumentException.class, () -> new Album(2018,"ECM 1064/65", "asdfghjkjhgfdsaw34rtyuijnbvcxz"));
    }

    @Test
    public void releaseYearSmallerThanNow(){
        assertTrue(album.releaseYearCompare(album.getReleaseYear()), "the release year smaller than this year");
    }

    @ParameterizedTest
    @ValueSource(ints = {1838,2100})
    public void releaseYearBiggerThanThisYear(int year){
        assertThrows(IllegalArgumentException.class, () -> album.setReleaseYear(year));
        assertThrows(IllegalArgumentException.class, () -> new Album(year,"ECM 1064/65", "The Köln Concert" ));
    }


    @Test
    public void AlbumFormat() throws MalformedURLException {
        URL urlAlbumFormat = new URL("https://www.google.com");
        assertThrows(IllegalArgumentException.class, () ->  album.setAlbumURL(urlAlbumFormat));
    }

    @Test
    public void lengthSizeCannotBeNullOfMusicianGroups(){
        List<Musician> Group = Lists.newArrayList();
        assertThrows(IllegalArgumentException.class, () -> album.setMusicianGroup(Group));
    }



    @ParameterizedTest
    @ValueSource(ints = 100)
    public void shouldReturnRightTimeLength(int arg) {
        album.setTimeLength(arg);
        assertEquals(arg, album.getTimeLength());
    }


    @ParameterizedTest
    @ValueSource(strings = {"Jazz"})
    public void shouldReturnRightGenre(String arg) {
        album.setGenre(arg);
        assertEquals(arg, album.getGenre());
    }


    @ParameterizedTest
    @ValueSource(strings = {"contemporary Jazz"})
    public void shouldReturnRightStyle(String arg) {
        album.setStyle(arg);
        assertEquals(arg, album.getStyle());
    }


    @ParameterizedTest
    @ValueSource(strings = {"CD", "LP"})
    public void shouldReturnRightReleaseFormat(String arg) {
        album.setReleaseFormat(arg);
        assertEquals(arg, album.getReleaseFormat());
    }


    @ParameterizedTest
    @ValueSource(doubles = {3.8d, 4.2d})
    public void shouldReturnRightRating(double arg) {
        album.setRating(arg);
        assertEquals(arg, album.getRating());
    }

    // Mengqian Wei Extra 2
    @ParameterizedTest
    @ValueSource(strings = {"good album", "bad album"})
    public void shouldReturnRightReviews(String arg) {
        album.setReviews(arg);
        assertEquals(arg, album.getReviews());
    }


}