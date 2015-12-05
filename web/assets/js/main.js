var reader = new FileReader();

reader.onload = function(e) {
    var lines = reader.result.split('\n');
    for(var line = 0; line < lines.length; line++){
      console.log(lines[line]);
    }
  //registeredReports.push(JSON.parse(text));
};

var file = new File("../server/data");
reader.readAsText(file);

for (var i = registeredReports.length - 1; i >= 0; i--) {
    console.log(registeredReports[i]);
};
