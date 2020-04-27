
//--- DATE FUNCTIONS -----------------------------------------------------------

Date.prototype.isValid = function () {
    // An invalid date object returns NaN for getTime() and NaN is the only
    // object not strictly equal to itself.
    return this.getTime() === this.getTime();
};
Date.prototype.daysFromNow = function (futureDate) {
    const oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
    var now = new Date();
    return Math.floor(Math.abs((futureDate - now) / oneDay));
};

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/DateTimeFormat
const utcOptions = {
        //weekday: 'long',
        year: 'numeric',
        month: 'short', 
        day: 'numeric',
        hour: 'numeric',
        hour12: false,
        minute: 'numeric',
        timeZoneName: 'long'
};
const utcFormatter = new Intl.DateTimeFormat('en-CA', utcOptions);

//--- FORM FUNCTIONS -----------------------------------------------------------

function setInputAndSubmit(existingFormId, inputName, inputValue) {
    $('input[name="' + inputName + '"]').remove();
    addInputAndSubmit(existingFormId, inputName, inputValue);
    return false;
}
function addInputAndSubmit(existingFormId, inputName, inputValue) {
    $('<input>', {
        type: 'hidden',
        name: inputName,
        value: inputValue,
    }).appendTo('#' + existingFormId);
    $('#' + existingFormId).submit();
    return false;
}
function removeInputAndSubmit(existingFormId, inputName, inputValue) {
    var el = $('#' + existingFormId).find(
            "input[value='" + $.escapeSelector(inputValue) + "']");
    if (!$.isEmptyObject(el)) {
        $(el).remove();
    }
    $('#' + existingFormId).submit();
    return false;
}

//--- STRING FUNCTIONS ---------------------------------------------------------
function stripHtml(html) {
    var tmp = document.createElement("DIV");
    tmp.innerHTML = html;
    return tmp.textContent || tmp.innerText || "";
}
function toString(obj) {
    return JSON.stringify(obj, null, 4);
}
function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

//--- ABOUT FUNCTIONS ----------------------------------------------------------

function aboutTryLink(linkSelector, callback) {
    $(linkSelector).addClass('btn btn-xs btn-outline-primary');
    if (typeof callback === "function") {
        $(linkSelector).click(function (e) {
            e.preventDefault();
            callback($(this));
        });
    }
}

//function tryTerm(terms) {
//    //TODO accept input field selector as argument and dynamically grab form
//    $('#terms').val(terms);
//    $('#searchForm').submit();
//}
//function suggestTerm(terms) {
//    //TODO accept input field selector as argument
//    $('#terms').val(terms);
//    $('#terms').keydown();
//    return false;
//}

//=== ON DOCUMENT READY ========================================================

$(document).ready(function () {

    // Converts all "utc" dates to user-local ones
    $.each($(".utc[data-utc]"), function() {
        var utcDate = new Date($(this).data('utc') + 'Z');
        if (utcDate.isValid()) {
            $(this).html(utcFormatter.format(utcDate));
        }
    });
    
    //--- Store collapse states ------------------------------------------------

    // Store/restore collapsed state between requests.
    // if there is a class "show-by-default" it will be shown only if
    // the state is not found in local storage.
    var storeKey = window.location.pathname + '-collapsible-states';
    var collapsibles = localStorage.getItem(storeKey);
    
    // If states were stored, restore them.
    if (collapsibles) {
        collapsibles = JSON.parse(collapsibles);
        $.each(collapsibles, function(id, state) {
            $('#' + id).collapse(state);
        });
    // If never stored, store them as they are on first render
    } else {
        collapsibles = {};
        $('.collapse').each(function() {
            var id = $(this).attr('id');
            if ($(this).hasClass('show-by-default')) {
                $(this).collapse('show');
                collapsibles[id] = 'show';
            } else {
                collapsibles[id] = $(this).hasClass('show') ? 'show' : 'hide';
            }
        });
        localStorage.setItem(storeKey, JSON.stringify(collapsibles));
    }

    // Store on every state change.
    $(document).on('shown.bs.collapse', '.collapse', function () {
        collapsibles[$(this).attr('id')] = 'show';
        localStorage.setItem(storeKey, JSON.stringify(collapsibles));
    }).on('hidden.bs.collapse', '.collapse', function() {
        collapsibles[$(this).attr('id')] = 'hide';
        localStorage.setItem(storeKey, JSON.stringify(collapsibles));
    });
    
//    //--- About "try" links ----------------------------------------------------
//    // Todo... use "data" to specify which input to use.
//    // use call back
//    $('.try-term').each(function () {
//        var text = $(this).text();
//        $(this).addClass('btn btn-xs btn-outline-primary');
//        $(this).click(function (e) {
//            e.preventDefault();
//            tryTerm(text);
//        });
//    });
//    $('.suggest-term').each(function () {
//        var text = $(this).text();
//        $(this).addClass('btn btn-xs btn-outline-primary');
//        $(this).click(function (e) {
//            e.preventDefault();
//            suggestTerm(text);
//        });
//    });

});

/*
function isBlank(str) {
  return (!str || /^\s*$/.test(str));
}
function formatSolrHtmlExplain(html) {
    var h = html;
    // score
    h = h.replace(/(<li>)(\d+\.?\d+)(E\-\d+)?(\s+=)/gi, 
            '$1<span class="hl-score">$2$3</span>$4');

    // operation (sum of, product of, from, etc.)
    h = h.replace(/(\W)([\s\w]*\:)(<br \/>)/gi, 
            '$1<span class="hl-operation">$2</span>$3');

    // highlight fields and query terms
    h = h.replace(/(=\s+weight\()(.*?)\:(.*?)(\s+in\s+\d+\))/gi, 
            '$1<span class="hl-field-qterm">'
          + '<span class="hl-field">$2</span>:'
          + '<span class="hl-qterm">$3</span></span>$4');

    // highlight functions
    h = h.replace(/(\W)([\w]+\()([\s\S]*?)(\))/gi, 
            '$1<span class="hl-function">$2</span>$3'
          + '<span class="hl-function">$4</span>');

    
    // key = value pairs
    h = h.replace(/(\w+)=([\w\.]+)/gi, 
            '<span class="hl-key">$1</span>='
          + '<span class="hl-value">$2</span>');

    // info 
    h = h.replace(/(\[.*?\])/gi, '<span class="hl-info">$1</span>');

    
//    var lines = h.split('\n');
//    for(var i = 0; i < lines.length; i++){
//        console.log("LINE: " + lines[i]);
//    }
    return h;
}

$.fn.selectRange = function(start, end) {
  return this.each(function() {
      if(this.setSelectionRange) {
          this.focus();
          this.setSelectionRange(start, end);
      } else if(this.createTextRange) {
          var range = this.createTextRange();
          range.collapse(true);
          range.moveEnd('character', end);
          range.moveStart('character', start);
          range.select();
      }
  });
};
function debounce(fn, delay) {
  var timer = null;
  return function () {
      var context = this, args = arguments;
      clearTimeout(timer);
      timer = setTimeout(function () {
          fn.apply(context, args);
      }, delay);
  };
}

function formatNumber(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


function safeTruncate(str, length) {
    if (!str || str.length <= length) {
        return str;
    }
    var s = str.substring(0, length);
    s = s.replace(/(.*)<[^>]*$/, '$1');
    return s + '<span class="truncated">...</span>';
}
*/
