package org.grails.rateable

import grails.util.*

class RateableTagLib {

    static namespace = 'rateable'

    def resources = {attrs ->
        out << yui.javascript(dir: 'yahoo-dom-event', file: 'yahoo-dom-event.js')
        out << yui.javascript(dir: 'connection', file: 'connection.js')
        out << yui.stylesheet(dir: 'yahoo-dom-event', file: 'yahoo-dom-event.js')
        out << """
        <script src=\"${createLinkTo(dir: pluginContextPath + '/js', file: 'ratings.js')}\"></script>
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
            5.times {cnt ->
                def i = cnt + 1
                if (average >= i) {
                    out << '<td><div class="star on"><a></a></div></td>'
                } else {
                    def starWidth = 100 * (average - (i - 1))
                    if (starWidth < 0) starWidth = 0
                    out << """<td><div class="star on"><a style="width:${starWidth}%"></a></div></td>"""
                }
            }
            out << """
                    <td>(${votes ?: 0})</td>
                </tr>
            </table>
            """
        } else {
            out << """
            <div id="${id}div">
                <form id="${id}" action="${createLink(controller: 'rateable', action: 'rate', id: bean.id, params: [type: type, xhr: true])}" method="post" title="${bean.averageRating}">
                    <label for="id_rating">Rating:</label>
                    <select name="${id}" id="id_${id}">
                        <option value="1">1 - Poor</option>
                        <option value="2">2 - Fair</option>
                        <option value="3">3 - Good</option>
                        <option value="4">4 - Very Good</option>
                        <option value="5">5 - Excellent</option>
                    </select>
                    <input id='ratingIsActive' type="hidden" value="true"/>
                    <input type="submit" value=" Submit rating"/>
                </form>
            </div>
            <div id='notifytext'>(${bean.totalRatings} Ratings)</div>
            """
        }
    }
}