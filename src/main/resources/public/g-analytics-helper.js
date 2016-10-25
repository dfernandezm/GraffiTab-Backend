/**
* Function that tracks a click on an outbound link in Analytics.
* This function takes a valid URL string as an argument, and uses that URL string
* as the event label. Setting the transport method to 'beacon' lets the hit be sent
* using 'navigator.sendBeacon' in browser that support it.
*/
var trackLink = function(url, label, blank) {
   ga('send', 'event', 'link', 'click', label, {
     'transport': 'beacon',
     'hitCallback': function(){
       if (blank) {
         window.open(url, '_blank');
       }
       else {
         window.location.href = url;
       }
     }
   });
}
