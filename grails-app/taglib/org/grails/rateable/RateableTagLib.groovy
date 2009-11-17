package org.grails.rateable

import grails.util.*

class RateableTagLib {

    static namespace = 'rateable'

    def resources = {attrs ->
        out << yui.javascript(dir: 'yahoo-dom-event', file: 'yahoo-dom-event.js')
        out << yui.javascript(dir: 'connection', file: 'connection.js')
        out << """
        <script type=\"text/javascript\" src=\"${resource(dir: pluginContextPath + '/js', file: 'ratings.js')}\"></script>
        <link rel=\"stylesheet\" href=\"${createLinkTo(dir: pluginContextPath + '/css', file: 'ratings.css')}\" />
        """
    }

    def ratings = {attrs ->
        if (!attrs.bean) throw new RatingException("There must be a 'bean' domain object included in the ratings tag.")
        def bean = attrs.bean
        def average = bean.averageRating ?: 0
        def votes = bean.totalRatings
        def type = GrailsNameUtils.getPropertyName(bean.class)
		def id = attrs.id ?: "rating"

        if (attrs.active == 'false') {
            out << """
            <table class="ratingDisplay">
                <tr>
            """
			def href = attrs.href ? "href=\"${attrs.href}\"" : ''
            5.times {cnt ->
                def i = cnt + 1
                if (average >= i) {
                    out << """<td><div class="star on"><a $href></a></div></td>"""
                } else {
                    def starWidth = 100 * (average - (i - 1))
                    if (starWidth < 0) starWidth = 0
                    out << """<td><div class="star on"><a $href style="width:${starWidth}%"></a></div></td>"""
                }
            }
            out << """
                    <td>(${votes ?: 0})</td>
                </tr>
            </table>
            """
        } else { // Rating is active
            out << """
            <div id="${id}_rating" class="star_rating">
                <form id="${id}_form" class="star_rating" action="${createLink(controller: 'rateable', action: 'rate', id: bean.id, params: [type: type, xhr: true])}" method="post" title="${average}">
                    <label for="${id}_select">Rating:</label>
                    <select name="rating" id="${id}_select">
                        <option value="1">1 - Poor</option>
                        <option value="2">2 - Fair</option>
                        <option value="3">3 - Good</option>
                        <option value="4">4 - Very Good</option>
                        <option value="5">5 - Excellent</option>
                    </select>
                    <input id="${id}_active" name='active' type="hidden" value="true"/>
                    <input type="submit" value="Submit Rating"/>
                </form>
            </div>
            <div id='${id}_notifytext'>(${votes ?: 0} Ratings)</div>
            """
        }
    }
}