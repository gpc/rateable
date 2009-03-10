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
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [hibernate:"1.1 > *", yui:"2.6.1 > *"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Matthew Taylor"
    def authorEmail = "matt.taylor@springsource.com"
    def title = "Rateable Plugin"
    def description = '''\\
A plugin that adds a generic mechanism for rating domain objects.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/rateable"

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
                    
                    rate = { rater, starRating ->
                        if (delegate.id == null) {
                            throw new RatingException("You must save the entity [${delegate}] before calling rate")
                        }
                        // try to find an existing rating to update
                        def r = delegate.ratings.find {
                            it.raterId == rater.id
                        }
                        // if there is no existing value, create a new one
                        if (!r) {
                            r = new Rating(stars:starRating, raterId:rater.id, raterClass:rater.class.name)
                            if (!r.validate()) {
                                throw new RatingException("Cannot create rating for args: [$rater, $starRating], they are invalid.")
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
                        if (delegate.id != null && delegate.ratings.size()) {
                            delegate.ratings*.stars.sum() / delegate.ratings.size()
                        } else {
                            return 0
                        }
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
