package nl.vpro.nicam;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * http://www.kijkwijzer.nl/about-kijkwijzer
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@Data
public class Kijkwijzer implements NicamRated {

    private AgeRating ageRating;
    private List<ContentRating> contentRatings;

    public static Kijkwijzer parse(String value) {
        AgeRating ageRating = null;
        List<ContentRating> contentRatings = new ArrayList<>();
        if (value != null) {
            for (char c : value.toCharArray()) {
                switch (c) {
                    case '2':
                        ageRating = AgeRating._6;
                        break;
                    case '3':
                        ageRating = AgeRating._9;
                        break;
                    case '4':
                        ageRating = AgeRating._12;
                        break;
                    case '5':
                        ageRating = AgeRating._16;
                        break;
                    default:
                        contentRatings.add(ContentRating.valueOf(c));

                }
            }
        }
        return new Kijkwijzer(ageRating, contentRatings);
    }

    // Used by Cinema
    public static Kijkwijzer parseDonna(String value){
        AgeRating ageRating = null;
        List<ContentRating> contentRatings = new ArrayList<>();
        if (value != null) {
            for (char c : value.toCharArray()) {
                switch (c) {
                    case '1':
                        ageRating = AgeRating.ALL;
                        break;
                    case '2':
                        ageRating = AgeRating._6;
                        break;
                    case '5':
                        ageRating = AgeRating._9;
                        break;
                    case '3':
                        ageRating = AgeRating._12;
                        break;
                    case '4':
                        ageRating = AgeRating._16;
                        break;
                    default:
                        contentRatings.add(ContentRating.valueOf(c));

                }
            }
        }
        return new Kijkwijzer(ageRating, contentRatings);
    }

    public Kijkwijzer(AgeRating ageRating, List<ContentRating> contentRatings) {
        this.ageRating = ageRating;
        this.contentRatings = contentRatings;
    }

    public Kijkwijzer() {
        this.ageRating = null;
        this.contentRatings = new ArrayList<>();
    }

    public String toDonnaCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toDonnaCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }

        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }

        return result.toString();
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }

        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }

        return result.toString();
    }

    public static Character toCode(AgeRating ageRating) {
        if (ageRating == null) {
            return null;
        }
        switch (ageRating) {

            case _6:
                return '2';
            case _9:
                return '3';
            case _12:
                return '4';
            case _16:
                return '5';
            default:
            case ALL:
                return null;
        }
    }
    public static Character toDonnaCode(AgeRating ageRating) {
        if (ageRating == null) {
            return null;
        }
        switch (ageRating) {

            case _6: return '2';
            case _9: return '5';
            case _12: return '3';
            case _16: return '4';
            default:
            case ALL:
                return '1';
        }
    }
}
