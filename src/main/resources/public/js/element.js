$.noConflict();
var $ = jQuery;
$( document ).ready(function($){
	/***/
	$('.awesome-screenshots').owlCarousel({
	   items:1,
	   loop:true,
	   center:true,
	   mouseDrag:false,
	   autoWidth:true,
	   margin:40,
	   nav:true,
	   dots:false,
	   responsive:{
	      767:{
	         items:5
	      },
	      600:{
	         items:3
	      },
	       360:{
	         items:1
	      }
	   }
	});

	/*bxSlider - client*/

	$('.client-slider').bxSlider({
		controls: false,
		auto: true
	});


	var Slides,
    width = $(window).width();

	if (width > 767){
	    Slides = 3;
	}
	if (width < 768) {
	    Slides = 2;
	}
	if (width < 559){
		Slides = 1;
	}

	/*bxSlider - team*/
	$('.team-slider').bxSlider({
	  minSlides: Slides,
	  maxSlides: Slides,
	  slideWidth: 360,
	  slideMargin: 10,
	  controls: false,
	  auto: true
	});

	/* spincrement */
	$('.counter').counterUp({
        delay: 10,
        time: 1000
    });
    /* smooth-scrolling	*/
    $(function() {
	  $('a[href*="#"]:not([href="#"])').on('click',function() {
	    if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
	      var target = $(this.hash);
	      target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
	      if (target.length) {
		    if($(window).width() > 1169){
		        $('html, body').animate({
		          scrollTop: target.offset().top - 72
		        }, 1000);
		        return false;
		    } else {
		    	$('html, body').delay(200).animate({
		          scrollTop: target.offset().top - 72
		        }, 1000);
		        return false;
		    }
	      }
	    }
	  });
	});

    /* Parallax */
    if ($(window).width() > 1024) {
	   	$('.above-fold').parallax("50%", 0.8);
		$('.features-main').parallax("50%", 0.2);
		$('.send-block-main').parallax("50%", 0.5);
		$('.team-block-main').parallax("50%", 0);
	}

	if ($(window).width() > 1024) {
		new WOW().init();
	};

	/* responsive menu */
	$( '.responsive-menu-btn' ).on('click',function() {
		$('#main-header').toggleClass('open-nav');
		$('body').toggleClass('hidden');
	});

	$( '#main-header nav ul li a' ).on('click',function() {
		$('#main-header').removeClass('open-nav');
		$('body').removeClass('hidden');
	});

	$('.responsive-menu-btn span').bind("touchmove touchstart",function(event){
		if ($('#main-header').hasClass('open-nav')) {
			event.preventDefault();
			$('#main-header').toggleClass('open-nav');
			$('body').toggleClass('hidden');
		}
	});
	$('.work-main #work-video').css("visibility", "hidden");
	$(".icon-ic_play_big").click(function() {
		var video = $("#work-video").get(0);
		video.play();
		$('.work-main #work-video').css("visibility", "visible");
		$(this).css("visibility", "hidden");
		$('.work-main h2').css("visibility", "hidden");
		return false;
	});
    // menu active js//
    $('#main-menu ul li a').click(function(e) {
        $('#main-menu ul li').removeClass('active');
        var $parent = $(this).parent();
        if (!$parent.hasClass('active')) {
            $parent.addClass('active');
        }
        e.preventDefault();
    });
});
/* Parallax */
$( window ).resize(function() {
  if ($(window).width() > 1024) {
	   	$('.above-fold').parallax("50%", 0.8);
		$('.features-main').parallax("50%", 0.2);
		$('.send-block-main').parallax("50%", 0.5);
		$('.team-block-main').parallax("50%", 0);
	} else {
		$('.above-fold').parallax("50%", 0);
		$('.features-main').parallax("50%", 0);
		$('.send-block-main').parallax("50%", 0);
		$('.team-block-main').parallax("50%", 0);
	}
});
$(window).scroll(function() {
    var scroll = $(window).scrollTop();
    if (scroll >= 250) {
        $("#main-header").addClass("darkHeader");
    } else {
        $("#main-header").removeClass("darkHeader");
    }
});
/* Parallax */
