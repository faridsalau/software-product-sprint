async function initMap() {
    const mapOptions = {
        // Oxford, MS
        center: {lat: 34.3668, lng: -89.5186},
        zoom: 14,
        minZoom: 14,
        mapTypeId: google.maps.MapTypeId.HYBRID,
        gestureHandling: "auto"
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

    const features = [
        {
            title:"My hometown!",
            icon: "http://maps.google.com/mapfiles/ms/micons/rangerstation.png",
            // Oxford, MS
            position: {lat: 34.3668, lng: -89.5186},
            infoWindow: true
        },
        {
            title: "My School!",
            icon: "http://maps.google.com/mapfiles/ms/micons/question.png",
            // University of Mississippi
            position: {lat: 34.3662, lng: -89.5380}
        }
    ]
    const infowindow = new google.maps.InfoWindow({
        content: await getMapContent()
    });
    features.forEach((_, index) => {
        var marker = new google.maps.Marker({
        map: map,
        draggable: false,
        animation: google.maps.Animation.DROP,
        position: features[index].position,
        icon: features[index].icon,
        title: features[index].title
        });

        if(features[index].infoWindow){
            marker.addListener('click', function() {
            infowindow.open(map, marker);
         });
       }
    });
    
}

async function getMapContent(){
    const data = await fetch("map-content.html");
    const html = await data.text();
    return html;
}
