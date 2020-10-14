package grails.plugins.rateable


import javax.annotation.PostConstruct

class RateableController  {
    
    private Closure evaluator

    RateableService rateableService

    @PostConstruct
    void init() {
        evaluator = grailsApplication.config.getProperty('grails.rateable.rater.evaluator', Closure, { request.user } )
    }

    def rate() {
        def rater = evaluateRater()

        Long ratingRef = params.id.toLong()
        Long raterId = rater.id.toLong()
        String type = params.type
        Double stars = params.rating.toDouble()
        rateableService.saveRating(ratingRef, type, raterId, stars, rater.class.name)

        def allRatings = RatingLink.withCriteria {
            projections {
                property 'rating'
            }
            eq "ratingRef", params.id.toLong()
            eq "type", params.type
            cache true
        }
        def avg = allRatings.size() ? allRatings*.stars.sum() / allRatings.size() : 0

        render "${avg},${allRatings.size()}"
    }

    def evaluateRater() {
		def rater 
		if(evaluator instanceof Closure) {
			evaluator.delegate = this
			evaluator.resolveStrategy = Closure.DELEGATE_ONLY
			rater = evaluator.call()
		}
		
		if(!rater) {
			throw new RatingException("No [grails.rateable.rater.evaluator] setting defined or the evaluator doesn't evaluate to an entity. Please define the evaluator correctly in grails-app/conf/application.groovy or ensure rating is secured via your security rules")
		}
		if(!rater.id) {
			throw new RatingException("The evaluated Rating rater is not a persistent instance.")
		}
		return rater
	}

    // def index() {
        
    //     def r1 = new TestRater(name:"fred").save()
    //     def r2 = new TestRater(name:"bob").save()       
    //     def r3 = new TestRater(name:"jack").save()
    //     def r4 = new TestRater(name:"joe").save()       
    //     def r5 = new TestRater(name:"ed").save()
    //     def r6 = new TestRater(name:"ted").save()       
        
    //     def iphone = new TestDomain(name:"iphone")
    //     def gone = new TestDomain(name:"gone")      
    //     def pre = new TestDomain(name:"pre")                
    //     def bold = new TestDomain(name:"bold")              
        
        
    //     bold.save()
    //     bold.rate(r1, 1)
    //         .rate(r2, 1)        
    //         .rate(r3, 1)        
    //         .rate(r4, 1)        
    //         .rate(r5, 1)        
    //         .rate(r6, 1)
                
    //     iphone.save()
    //     iphone.rate(r1, 5)
    //           .rate(r2, 5)      
    //           .rate(r3, 5)      
    //           .rate(r4, 5)      
    //           .rate(r5, 5)      
    //           .rate(r6, 5)      


    //     gone.save()
    //     gone.rate(r1, 1)
    //         .rate(r2, 2)        
    //         .rate(r3, 1)        
    //         .rate(r4, 3)        
    //         .rate(r5, 4)        
    //         .rate(r6, 1)        


    //     pre.save()
    //  3   pre.rate(r1, 4)
    //         .rate(r2, 3)        
    //         .rate(r3, 4)        
    //         .rate(r4, 5)        
    //         .rate(r5, 4)        
    //         .rate(r6, 5)    

    //     [test: pre]    
    // }    
}