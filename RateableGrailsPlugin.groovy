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
 import org.grails.rateable.*
 import grails.util.*
 
 class RateableGrailsPlugin {
    def version = "0.7.1"
    def grailsVersion = "1.2 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Matthew Taylor"
    def authorEmail = "matthew@g2one.com"
    def title = "Rateable Plugin"
    def description = "A plugin that adds a generic mechanism for rating domain objects."

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/rateable"

    def license = "APACHE"
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPRATEABLE" ]
    def scm = [ url: "http://svn.codehaus.org/grails-plugins/grails-rateable/" ]

    def doWithSpring = {
        def config = application.config
		
		if(!config.grails.rateable.rater.evaluator) {
			config.grails.rateable.rater.evaluator = { request.user }
		}
    }

    def doWithDynamicMethods = { ctx ->
        for (domainClass in application.domainClasses) {
            if (Rateable.class.isAssignableFrom(domainClass.clazz)) {
                domainClass.clazz.metaClass {
	
					
					'static' {
						listOrderByAverageRating { Map params = [:] ->
							if(params==null) params =[:]
							def clazz = delegate
							def type = GrailsNameUtils.getPropertyName(clazz)
							if(params.cache==null) params.cache=true
							def results = clazz.executeQuery("select r.ratingRef,avg(rating.stars),count(rating.stars) as c from RatingLink as r join r.rating rating where r.type='$type' group by r.ratingRef order by count(rating.stars) desc ,avg(rating.stars) desc", params)
							def instances = clazz.withCriteria {  
								inList 'id', results.collect { it[0] } 
								cache params.cache
							}
							results.collect {  r-> instances.find { i -> r[0] == i.id } }							
						}

						countRated {->
							def clazz = delegate
							RatingLink.createCriteria().get {
								projections {
									countDistinct "ratingRef"
								}
								eq "type", GrailsNameUtils.getPropertyName(clazz)
								cache true
							}
						}						
					}
                    
                    rate = { rater, Double starRating ->
                        if (delegate.id == null) {
                            throw new RatingException("You must save the entity [${delegate}] before calling rate")
                        }
                        // try to find an existing rating to update
						def instance = delegate
                        def r = RatingLink.createCriteria().get {
							projections { property "rating" }
							rating {
								eq 'raterId', rater.id
							}
							eq "ratingRef", instance.id
                            eq "type", GrailsNameUtils.getPropertyName(instance.class)
							cache true
						}
						
                        // if there is no existing value, create a new one
                        if (!r) {
                            r = new Rating(stars:starRating, raterId:rater.id, raterClass:rater.class.name)
                            if (!r.validate()) {
                                throw new RatingException("Cannot create rating for args: [$rater, $starRating], they are invalid. ")
                            }
                            r.save()
                            def link = new RatingLink(rating:r, ratingRef:delegate.id, type:GrailsNameUtils.getPropertyName(delegate.class))
                            link.save()
                        }
                        // for an existing rating, just update the star value 
                        else {
                            r.stars = starRating
                        }
                        return delegate
                    }
                    
                    getRatings = { ->
                        def instance = delegate
                        if (instance.id != null) {
                            RatingLink.withCriteria {
                                projections {
                                    property "rating"
                                }
                                eq "ratingRef", instance.id
                                eq "type", GrailsNameUtils.getPropertyName(instance.class)
                                cache true
                            }
                        } else {
                            return Collections.EMPTY_LIST
                        }
                    }
                    
                    getAverageRating = { ->
						def instance = delegate
						def result = RatingLink.createCriteria().get {
							rating {
								projections { avg 'stars' }
							}
							eq "ratingRef", instance.id
                            eq "type", GrailsNameUtils.getPropertyName(instance.class)								
							cache true
						}
						result
                    }
                    
                    getTotalRatings = { ->
                        def instance = delegate
                        if (instance.id != null) {
                            RatingLink.createCriteria().get {
                                projections {
                                    rowCount()
                                }
                                eq "ratingRef", instance.id
                                eq "type", GrailsNameUtils.getPropertyName(instance.class)
                                cache true
                            }
                        } else {
                            return 0
                        }
                    }

                    userRating = { user ->
                        if (!user) return
                        def instance = delegate
                        RatingLink.withCriteria {
                            createAlias("rating", "r")
                            projections {
                                property "rating"
                            }
                            eq "ratingRef", instance.id
                            eq "type", GrailsNameUtils.getPropertyName(instance.class)
                            eq "r.raterId", user.id
                            cache true
                        }
                    }
                }
            }
        }
    }
}
