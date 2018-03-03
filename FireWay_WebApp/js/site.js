var deviceAgent = navigator.userAgent.toLowerCase();
var iOS = deviceAgent.match(/(iphone|ipod|ipad)/);
var htmlScrollbar;

document.body.className = document.body.className.replace(/\bno\-js\b/gi, "");

$(document).ready(function () {

	// initialize custom scrollbar
	if (!iOS) {
		htmlScrollbar = $("html").niceScroll({
			cursoropacitymax   : 0.8,
			cursorcolor        : '#000',
			cursorwidth        : "8px",
			cursorborder       : 'none',
			cursorborderradius : '6px',
			cursorminheight    : 50,
			mousescrollstep    : 50,
			grabcursorenabled  : false,
			dblclickzoom       : false
		});
	}

	// initialize tooltips
	$('.tooltip').tipsy({
		fade    : true,
		live    : true,
		opacity : 0.8,
		offset  : 4,
		gravity : $.fn.tipsy.autoNS
	});

	$('form .help').tipsy({
		trigger  : 'focus',
		opacity  : 0.8,
		offset   : 2,
		gravity  : $.fn.tipsy.autoWE
	});

	$('.tabs').tabify();

	// initialize supersized plugin, only if it's placed in the markup
	if ($("#supersized").length > 0) {
		$.supersized({
			horizontal_center : 1,
			vertical_center   : 1,
			slideshow         : 1,
			autoplay          : 0,
			transition        : 1,
			transition_speed  : 500,
			image_protect     : 1,
			slides            : supersized_slides
		});

		theme = {
			beforeAnimation : function(state) {
				$('#supersized-info h2 a')
					.html(api.getField('article_title'))
					.attr('href',api.getField('article_url'));
				$('#supersized-info p').html(api.getField('article_text'));
			}
		}
		$('.supersized-prev, .supersized-next').click( function (e) {
			if ($(this).hasClass("supersized-next")) {
				api.nextSlide();
			} else if ($(this).hasClass("supersized-prev")) {
				api.prevSlide();
			}
			e.preventDefault();
		});

		$('.supersized-fullscreen').click( function (e) {
			$('body').toggleClass("expand-supersized");
			if ($('#header').hasClass("hide-menu")) {
				$('#main,#footer').show();
				$('#supersized').css('right', '');
				$(window).trigger('resize');
			} else {
				$('#supersized').css('right', 0);
				$('#main,#footer').hide();
				$(window).trigger('resize');
			}
			$('#header').toggleClass('hide-menu');
			e.preventDefault();
		});
	}

	$('.accordion .accordion-title').click(function(e){
		$li = $(this).parent('li');
		$ul = $li.parent('.accordion');
		// check if only one accordion can be opened at a time
		if ($ul.hasClass('only-one-visible')) {
			$('li',$ul).not($li).removeClass('active');
		}
		$li.toggleClass('active');
		e.preventDefault();
	});

	$('#menu li').hover(
		function () {
			$(this).addClass("hover");
		},
		function () {
			$(this).removeClass("hover");
		}
	);

	$('#menu li.arrow a').click( function (e) {
		$el = $(this).parent();
		if ($el.hasClass('arrow')) {
			$el.toggleClass("hover");
			$el.toggleClass('expand-menu');
			if (!iOS) htmlScrollbar.resize();
			e.preventDefault();
		}
	});

	$('.menu-toggle').click(function(e) {
		$('#header').toggleClass('hide-menu');
		if (!iOS) htmlScrollbar.resize();
	});

	$top_link = $('#top-link');
	$top_link.click(function(e) {
		$('html, body').animate({scrollTop:0}, 750, 'linear');
		e.preventDefault();
		return false;
	});

});