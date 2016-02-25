var registeredReports = [];
var coordinates = {lat: 63.825689246915395, lng: 20.264078378677368, content: "This is more content"};
var map;

var MapStyles = [
    {
        "featureType": "administrative",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#444444"
            }
        ]
    },
    {
        "featureType": "landscape",
        "elementType": "all",
        "stylers": [
            {
                "color": "#f2f2f2"
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "all",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "poi.attraction",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "visibility": "on"
            },
            {
                "color": "#f8a724"
            }
        ]
    },
    {
        "featureType": "poi.medical",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "color": "#65ba69"
            }
        ]
    },
    {
        "featureType": "road",
        "elementType": "all",
        "stylers": [
            {
                "saturation": -100
            },
            {
                "lightness": 45
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "all",
        "stylers": [
            {
                "visibility": "simplified"
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "color": "#f8a724"
            }
        ]
    },
    {
        "featureType": "road.arterial",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "visibility": "on"
            },
            {
                "color": "#f8a724"
            }
        ]
    },
    {
        "featureType": "road.arterial",
        "elementType": "labels.icon",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "road.local",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#f8a724"
            },
            {
                "visibility": "on"
            }
        ]
    },
    {
        "featureType": "transit",
        "elementType": "all",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "water",
        "elementType": "all",
        "stylers": [
            {
                "color": "#253137"
            },
            {
                "visibility": "on"
            }
        ]
    }
]

var COUNTIES = {
    norbotten: "Norrbotten",
    lappland: "Lappland",
    vasterbotten: "Vasterbotten",
    uppland: "Uppland",
    sodermanland: "Sodermanland",
    vastmanland: "Vastmanland",
    ostergotland: "Ostergotland",
    jamtland: "Jamtland",
    dalarna: "Dalarna",
    gastrikland: "Gastrikland",
    narke: "Narke",
    varmland: "Varmland",
    vastergotland: "Vastergotland",
    dalsland: "Dalsland",
    bohuslan: "Bohuslan",
    gotland: "Gotland",
    smaland: "Smaland",
    oland: "Oland",
    blekinge: "Blekinge",
    skane: "Skane",
    halland: "Halland",
    angermanland: "Angermanland",
    harjedalen: "Harjedalen",
    medelpad: "Medelpad",
    halsingland: "Halsingland"

}