/* Copyright 2009 Matthew Taylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugins.rateable


import grails.util.GrailsNameUtils

/**
 * Trait to make a domain class rateable
 *
 * @author Graeme Rocher
 * @author Matthew Taylor
 */
trait Rateable {

    Rateable rate( rater, Double starRating ) {
        if (this.id == null) {
            throw new RatingException("You must save the entity [${this}] before calling rate")
        }
        // try to find an existing rating to update
		def instance = this
		def criteria = RatingLink.createCriteria()
        def r = criteria.get {
			criteria.projections { criteria.property "rating" }
			criteria.rating {
				criteria.eq 'raterId', rater.id
			}
			criteria.eq "ratingRef", instance.id
            criteria.eq "type", GrailsNameUtils.getPropertyName(instance.class)
			criteria.cache true
		}
		
        // if there is no existing value, create a new one
        if (!r) {
            r = new Rating(stars:starRating, raterId:rater.id, raterClass:rater.class.name)
            if (!r.validate()) {
                throw new RatingException("Cannot create rating for args: [$rater, $starRating], they are invalid. ")
            }
            r.save()
            def link = new RatingLink(rating:r, ratingRef:this.id, type:GrailsNameUtils.getPropertyName(this.class))
            link.save()
        }
        // for an existing rating, just update the star value 
        else {
            r.stars = starRating
        }
        return this
    }
    
    List<Rating> getRatings() {
        def instance = this
        if (instance.id != null) {
        	def criteria = RatingLink.createCriteria()
            criteria.list {
                criteria.projections {
                	criteria.property "rating"
                }
                criteria.eq "ratingRef", instance.id
                criteria.eq "type", GrailsNameUtils.getPropertyName(instance.class)
                criteria.cache true
            }
        } else {
            return Collections.EMPTY_LIST
        }
    }
    
    Double getAverageRating () {
    	def instance = this
    	def criteria = RatingLink.createCriteria()
		def result = criteria.get {
			criteria.rating {
				criteria.projections { criteria.avg 'stars' }
			}
			criteria.eq "ratingRef", instance.id
            criteria.eq "type", GrailsNameUtils.getPropertyName(instance.class)								
			criteria.cache true
		}
		result
    }
    
    Integer getTotalRatings() {
        def instance = this
        if (instance.id != null) {
        	def criteria = RatingLink.createCriteria()
            criteria.get {
                criteria.projections {
                	criteria.rowCount()
                }
                criteria.eq "ratingRef", instance.id
                criteria.eq "type", GrailsNameUtils.getPropertyName(instance.class)
                criteria.cache true
            }
        } else {
            return 0
        }
    }

    List<Rating> userRating( user ) {
        if (!user) return
        def instance = this
        def criteria = RatingLink.createCriteria()
        criteria.list {
            criteria.createAlias("rating", "r")
            criteria.projections {
            	criteria.property "rating"
            }
            criteria.eq "ratingRef", instance.id
            criteria.eq "type", GrailsNameUtils.getPropertyName(instance.class)
            criteria.eq "r.raterId", user.id
            criteria.cache true
        }
    }

	static List<Rateable> listOrderByAverageRating( Map params = [:] ) {
		if(params==null) params =[:]
		def clazz = this
		def type = GrailsNameUtils.getPropertyName(clazz)
		if(params.cache==null) params.cache=true
		def results = clazz.executeQuery("select r.ratingRef,avg(rating.stars),count(rating.stars) as c from RatingLink as r join r.rating rating where r.type='$type' group by r.ratingRef order by count(rating.stars) desc ,avg(rating.stars) desc".toString(), params)
		def criteria = clazz.createCriteria()
		def instances = criteria.list {  
			criteria.inList 'id', results.collect { it[0] } 
			criteria.cache params.cache
		}
		results.collect {  r-> instances.find { i -> r[0] == i.id } }							
	}

    static Rateable getTopRated( Map params = [:] ) {
        if(params==null) params =[:]
        def clazz = this
        def type = GrailsNameUtils.getPropertyName(clazz)
        if(params.cache==null) params.cache=true
        def results = clazz.executeQuery("select r.ratingRef,avg(rating.stars),count(rating.stars) as c from RatingLink as r join r.rating rating where r.type='$type' group by r.ratingRef order by count(rating.stars) desc ,avg(rating.stars) desc".toString(), params)
        return get(results[0][0])
    }    

	static Integer countRated () { 
		def clazz = this
		def criteria = RatingLink.createCriteria()
		criteria.get {
			criteria.projections {
				criteria.countDistinct "ratingRef"
			}
			criteria.eq "type", GrailsNameUtils.getPropertyName(clazz)
			criteria.cache true
		}
	}		
}