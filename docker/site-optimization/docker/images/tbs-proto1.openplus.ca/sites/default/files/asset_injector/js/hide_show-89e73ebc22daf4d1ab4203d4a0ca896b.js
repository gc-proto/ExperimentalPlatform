(function($) {

   $('.showMore').click(function(){
       $(this).siblings('.mores').toggleClass('hidden');
   });
   
    $('.showResults').click(function(){
       $( '.results').show();
   });
   
   
   
     $('.quebec-f').click(function(){
       $( '.quebec').show( );
       $( '.ontario').hide( );
       $( '.ncr').hide( );
        $( '.4-result').hide( );
       $( '.1-result').toggleClass('wb-inv');

 
     });
    
     $('.ontario-f').click(function(){
       $( '.quebec').hide( );
       $( '.ontario').show( );
       $( '.ncr').hide( );
       $( '.4-result').hide( );
       $( '.1-result').toggleClass('wb-inv');
     });
   
     $('.ncr-f').click(function(){
       $( '.quebec').hide( );
       $( '.ontario').hide( );
       $( '.ncr').show( );
       $( '.4-result').hide( );
       $( '.1-result').toggleClass('wb-inv');
      
   });
   
   $('.proofreading-f').click(function(){
       $( '.public-opinion').hide( );
       $( '.proofreading').show( );
       $( '.4-result').hide( );
       $( '.1-result').toggleClass('wb-inv');
      
   });
   
      $('.public-opinion-f').click(function(){
      $( '.proofreading').hide( );
       $( '.public-opinion').show( );
       $( '.4-result').hide( );
       $( '.1-result').toggleClass('wb-inv');
      
   });
   
    $('.training-f').click(function(){
      $( '.dev').hide( );
      $( '.software').hide( );
      $( '.dev-2').hide( );
      $( '.training').show( );
    });
      
    $('.dev-2-f').click(function(){
      $( '.dev').hide( );
      $( '.software').hide( );
      $( '.dev-2').show( );
      $( '.training').hide( );
      
    });
      
    $('.dev-f').click(function(){
      $( '.dev').show( );
      $( '.software').hide( );
      $( '.dev-2').show( );
      $( '.training').hide( );
      
    });
      
    $('.software-f').click(function(){
      $( '.dev').hide( );
      $( '.software').show( );
      $( '.dev-2').hide( );
      $( '.training').hide( );
    
      
   });
   
    $('#search-options').on('change', function() {
    
     	if ( this.value == '1')
      {
        $(".goods").hide();
        $(".services").show();
        $(".goods-results").hide();
        $(".all-results").hide();
        $(".services-results").show();

      }
      
      else if ( this.value == '2')
      {
        $(".services").hide();
        $(".goods").show();
        $(".services-results").hide();
        $(".all-results").hide();
        $(".goods-results").show();

      }
      else
      {
        $(".services").show();
        $(".goods").show();
        $(".all-results").show();
      }
    });
 
  
})(jQuery);