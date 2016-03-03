
function load() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;
            coordinates.lat = lat;
            coordinates.lng = lng;
            initialize(lat, lng);
        });
    }
}

function initialize(lat, lang) {
    var mapCanvas = document.getElementById('map');
    var mapOptions = {
      center: new google.maps.LatLng(coordinates.lat, coordinates.lng),
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      styles: MapStyles,
    }
    map = new google.maps.Map(mapCanvas, mapOptions);
}

function placeMarkersOnMap(){
    console.log("placeMarkersOnMap()");
    for(var i = 0; i < registeredReports.length; ++i) {
        var current = registeredReports[i];
        var latLng = new google.maps.LatLng(coordinates.lat, coordinates.lng);
        var descriptionString = current.text;
        var timeStamp = new Date(current.time*1000);
        var imgString = parseImage(current.photo);

        var contentString = createContentString(descriptionString, timeStamp, imgString);

        var myInfowindow = new google.maps.InfoWindow({
            content: contentString
          });
        
        var marker = new google.maps.Marker({
            position: latLng,
            map: map,
            title: 'Umeå universitet '+i,
            infowindow: myInfowindow,
            icon: 'assets/js/pin.png'
        });
        
        google.maps.event.addListener(marker, 'click', function() {
            this.infowindow.open(map, this);
        });

    }
}

google.maps.event.addDomListener(window, 'load', load);


