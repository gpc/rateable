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
 package org.grails.rateable 
 
 class Rating {
     Double stars
     Date dateCreated
     Date lastUpdated
     Long raterId
     String raterClass
     
     def getRater() {
         getClass().classLoader.loadClass(raterClass).get(raterId)
     }
     
     static constraints = {
         raterClass blank:false
         raterId min: 0L
     }

	 static mapping = {
		cache true
	}
     
     String toString() {
         "$rater voted $stars"
     }
 }