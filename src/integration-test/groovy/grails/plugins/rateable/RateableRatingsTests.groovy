package grails.plugins.rateable

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

import static org.junit.Assert.assertEquals

@Rollback
@Integration
class RateableRatingsTests extends Specification {

	void testAverageRating() {
		when:
		def r1 = new TestRater(name:"fred").save()
		def r2 = new TestRater(name:"bob").save()		
		
		def test = new TestDomain(name:"thing")
		test.save()
		test.rate(r1, 4)
		    .rate(r2, 2)		
		
		def stuff = new TestDomain(name:'stuff')
		stuff.save()
		stuff.rate(r1, 1)
		     .rate(r2, 2)

		then:
		assert 3 == test.averageRating
		assert 1.5 == stuff.averageRating
		assert 2, TestDomain.countRated()
	}
	
	void testGetTopRated() {
		when:
		def r1 = new TestRater(name:"fred").save()
		def r2 = new TestRater(name:"bob").save()		
		def r3 = new TestRater(name:"jack").save()
		def r4 = new TestRater(name:"joe").save()		
		def r5 = new TestRater(name:"ed").save()
		def r6 = new TestRater(name:"ted").save()		
		
		def iphone = new TestDomain(name:"iphone")
		def gone = new TestDomain(name:"gone")		
		def pre = new TestDomain(name:"pre")				
		def bold = new TestDomain(name:"bold")				
		
		
		bold.save()
		bold.rate(r1, 1)
    	    .rate(r2, 1)		
		    .rate(r3, 1)		
		    .rate(r4, 1)		
		    .rate(r5, 1)		
		    .rate(r6, 1)
				
		iphone.save()
		iphone.rate(r1, 5)
		      .rate(r2, 5)		
		      .rate(r3, 5)		
		      .rate(r4, 5)		
		      .rate(r5, 5)		
		      .rate(r6, 5)		


		gone.save()
		gone.rate(r1, 1)
    	    .rate(r2, 2)		
		    .rate(r3, 1)		
		    .rate(r4, 3)		
		    .rate(r5, 4)		
		    .rate(r6, 1)		


		pre.save()
		pre.rate(r1, 4)
    	    .rate(r2, 3)		
		    .rate(r3, 4)		
		    .rate(r4, 5)		
		    .rate(r5, 4)		
		    .rate(r6, 5)	

		
		def topRated = TestDomain.listOrderByAverageRating()	
		
		println topRated
		then:
		assertEquals "iphone", topRated[0].name
		assertEquals "pre", topRated[1].name
		assertEquals "gone", topRated[2].name
		assertEquals "bold", topRated[3].name	
		assert TestDomain.topRated.name == 'iphone'					
		
	}

}