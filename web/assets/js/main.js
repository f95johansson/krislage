function recieveServerData() {
    $.get( "../server/data" , function(data){
        var lines = data.split('\n');
        if (lines.length > registeredReports.length) {
            for (var i = registeredReports.length; i < lines.length; ++i) {
                registeredReports.push(JSON.stringify(lines[i]));
            };
        };
    });
}

setInterval(recieveServerData, 2000);

setTimeout(placeMarkersOnMap, 3000);