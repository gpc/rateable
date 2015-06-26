package grails.plugins.rateable

import grails.util.*

class RateableTagLib {

    static namespace = 'rateable'


    def ratings = {attrs ->
        if (!attrs.bean) throw new RatingException("There must be a 'bean' domain object included in the ratings tag.")
        def bean = attrs.bean
        def average = bean.averageRating ?: 0
        def votes = bean.totalRatings
        def type = GrailsNameUtils.getPropertyName(bean.class)
		def id = attrs.id ?: "rating"

        if (attrs.active == 'false') {
            out << g.render(template:'/rateable/ratingWidget', model:[average: average, votes: votes, type:type, id: id])

        } else { // Rating is active
            out << g.render(template:'/rateable/ratingWidget', model:[average: average, votes: votes, type:type, id: id])
        }
    }
}