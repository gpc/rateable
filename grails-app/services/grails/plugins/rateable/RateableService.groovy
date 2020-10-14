package grails.plugins.rateable


import grails.gorm.transactions.Transactional

@Transactional
class RateableService {

    void saveRating(Long ratingRef, String type, Long raterId, Double stars, String raterClassName) {
        // for an existing rating, update it
        Rating rating = RatingLink.createCriteria().get {
            createAlias("rating", "r")
            projections {
                property "rating"
            }
            eq "ratingRef", ratingRef
            eq "type", type
            eq "r.raterId", raterId
            cache true
        }
        if (rating) {
            rating.stars = stars
            assert rating.save()
        }
        // create a new one otherwise
        else {
            // create Rating
            rating = new Rating(stars: stars, raterId: raterId, raterClass: raterClassName)
            assert rating.save()
            def link = new RatingLink(rating: rating, ratingRef: ratingRef, type: type)
            assert link.save()
        }
    }
}
