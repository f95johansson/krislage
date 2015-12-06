function recieveServerData() {
    $.get( "../server/data" , function(data){
        var lines = data.split('\n');
        if (lines.length > registeredReports.length) {
            for (var i = registeredReports.length; i < lines.length-1; ++i) {
                registeredReports.push(jQuery.parseJSON(lines[i]));
            };
        };
    });
}

function parseImage(imgString) {
    return "<img alt='' class='reportImage' src='data:image/jpeg;base64,"+ imgString+ "'/>";
}
function createContentString(descriptionString, timeStamp, imgString) {
    return "<p><b>Description:</b> "+descriptionString+"<br>"+"<b>Time:</b> "+timeStamp+"</p>\n"+imgString;
}

setInterval(recieveServerData, 2000);

setInterval(placeMarkersOnMap, 3000);
