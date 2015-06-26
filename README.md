Rateable Grails Plugin
=========================

This is the source for the [Rateable Grails plugin][1] which adds support for allowing users to rate things, such as plugins, books, etc.

This plugin provides allows ratings to be attached to domain objects, as well as a 5-star rating component with ajax update.

## Requirements

* Grails Version: 3.0 and above
* JDK: 1.7 and above


## Installation

Add the following to `build.gradle`:

     compile 'org.grails.plugins:rateable:2.0.0-SNAPSHOT'


## Usage

Implement the @Rateable@ trait:


     import grails.plugins.rateable.*
     
     class Vehicle implements Rateable {
     }

On your page load the rateable resources:

     <asset:stylesheet href="ratings.css"/>
     <asset:javascript src="ratings.js"/>

Include a tag to allow users to rate:

     <rateable:ratings bean='${myVehicle}'/>

You may need to define a rater evaluator in grails-app/conf/application.groovy. The default one looks like:

     grails.rateable.rater.evaluator = { request.user }

But if you store users in the session instead you may want this to be:

     grails.rateable.rater.evaluatorr = { session.user }

The plugin also adds some useful static methods and properties to each `Rateable` as defined below:

### Static Methods

* *listOrderByAverageRating* - lists all rated items by their average rating. Takes an optional Map parameter for pagination
* *countRated* -  Counts the number of rated items, good for pagination in combination with the above method.
* *topRated* -  The top rated item

### Properties

* *ratings* - Returns all the ratings for a given Rateable
* *averageRating* -  Returns the average rating of all the ratings
* *totalRatings* - Returns the total number of ratings given to the Rateable

### Methods 

* *rate(user, Double rating)* - Rates a Rateable for the given user and specified rating
* *userRating(user)* - Returns the rating for the specified user

[1]: http://grails.org/plugin/rateable
