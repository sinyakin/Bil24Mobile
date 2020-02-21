ymaps.ready(init);

function init() {
    var scripts = document.getElementById('yandexScript');
    var lat = scripts.getAttribute("data-lat");
    var lng = scripts.getAttribute("data-lng");
    var venueName = scripts.getAttribute("data-venueName");
    var venueAddress = scripts.getAttribute("data-venueAddress");

    var myMap = new ymaps.Map("map", {
            center: [lat, lng],
            zoom: 15
        }, {
            suppressMapOpenBlock: true /*убирает кнопку Как добраться*/
        });

    myMap.controls.remove('trafficControl');
    myMap.controls.remove('typeSelector');
    myMap.controls.remove('fullscreenControl');
    myMap.controls.remove('rulerControl');
    myMap.controls.remove('searchControl');

    myMap.geoObjects
        .add(new ymaps.Placemark([lat, lng], {
            iconCaption: venueName,
            balloonContent: venueAddress
        }, {
            preset: 'islands#dotIcon',
            iconColor: '#735184'
        }));
}