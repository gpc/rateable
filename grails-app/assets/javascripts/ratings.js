//= require jquery
//= require_self

$( document ).ready( function() {
	$.each($('fieldset label.star'), function( index, value ) {
		value.onclick = function( event) {
			var label = $( event.target );
			var form = label.closest('form');
			var ratingVal = $('input[id="' + label.attr('for') + '"]', form).val();
			$('input[name="rating"]', form).val(ratingVal);
			$.ajax( {
				type: "POST",
				url: form.attr( 'action' ),
				data: form.serialize(),
				success: function( response ) {
					console.log( response );
				}
			} );
		}
	});
})


