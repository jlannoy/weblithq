jQuery.fn.preventDoubleSubmission = function() {
  $(this).on('submit',function(e){
    var $form = $(this);

    if ($form.data('submitted') === true) {
      // Previously submitted - don't submit again
      e.preventDefault();
    } else {
      // Mark it so that the next submit can be ignored
      $form.data('submitted', true);
    }
  });

  // Keep chainability
  return this;
};

$(document).ready(function() {
    $('.ui.multiple.dropdown').dropdown({ sortSelect : true });
    $('.ui.required.search.dropdown').dropdown({ fullTextSearch : true, match : 'text' });
    $('.ui.notRequired.search.dropdown').dropdown({ fullTextSearch : true, match : 'text', clearable : true });
    $('.ui.required.select.dropdown').dropdown(); 
    $('.ui.notRequired.select.dropdown').dropdown({ clearable : true });
    $('.ui.pointing.dropdown').dropdown(); // On icons
    $('.ui.attached.dropdown').dropdown(); // Top menu
    $('.ui.checkbox').checkbox();
    $('.tabular.menu .item').tab();
    $('.accordion').accordion();
    //initCalendar();
    //initHelpModal();
});

$(window).on('load', function(){
    $('.popup').popup({ position : 'bottom center' });
    $('.clickpopup').popup({ position : 'bottom center', on : 'click' });
    //autoTableDblClick();
    $('form').preventDoubleSubmission();
});

function showLoading() {
    $('#loadingDimmer').addClass('active');
}

function hideLoading() {
    $('#loadingDimmer').removeClass('active');
}

function formatCalendarDate(date, lang) {
    var d = new Date(date || Date.now());
    var month = '' + (d.getMonth() + 1);
    var day = '' + d.getDate();
    var year = lang == 'fr' ? d.getFullYear() : d.getFullYear().toString().substring(2);

    if (month.length < 2)
     month = '0' + month;
    if (day.length < 2)
     day = '0' + day;
     
    if(lang == 'fr') {
      return [ day, month, year ].join('/');
    } else {
      return [ month, day, year ].join('/');
    }
}