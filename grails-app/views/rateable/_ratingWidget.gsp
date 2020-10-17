<g:form id="${id}" url="[controller:'rateable', action:'rate', id: bean.id]" method="POST" class="ratingForm">
	<input type="hidden" name="id" value="${bean.id}" />
	<input type="hidden" name="type" value="${type}" />
	<input type="hidden" name="rating"/>

	<fieldset class="rating">
		<g:set var="checkedSet" value="${false}" />
		<g:each in="${10..1}" var="i">
			<g:set var="j" value="${i/2}" />
			<input type="radio" id="${type}-${bean.id}star${j}" name="${type}-${bean.id}" class="star" value="${j}" ${average >= j  && !checkedSet ? raw('checked="true"') : ''} /><label class = "${ i % 2 == 0 ? 'full' : 'half' } star" for="${type}-${bean.id}star${j}" title="${j} stars"></label>
			<g:if test="${average >= j }">
				<g:set var="checkedSet" value="${true}" />
			</g:if>
		</g:each>
	</fieldset>
</g:form>