<form id="${id}" class="rating">
	<fieldset class="rating">		
		<g:set var="checkedSet" value="${false}" />
		<g:each in="${10..1}" var="i">
			<g:set var="j" value="${i/2}" />
			<input type="radio" id="star${j}" name="rating" value="${j}" ${average >= j  && !checkedSet ? raw('checked="true"') : ''} /><label class = "${ i % 2 == 0 ? 'full' : 'half' }" for="star${j}" title="${j} stars"></label>			
			<g:if test="${average >= j }">
				<g:set var="checkedSet" value="${true}" />
			</g:if>
		</g:each>
	</fieldset>
</form>