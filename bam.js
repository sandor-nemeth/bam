var express = require('express');
var app = express();
var fs = require("fs");


var bodyParser = require('body-parser');
app.use(bodyParser.json());

app.put('/agent', function (req, res) {
    console.log("put received");
    // First read existing users.
    //fs.readFile( __dirname + "/" + "users.json", 'utf8', function (err, data) {
    //    data = JSON.parse( data );
    //    data["user4"] = user["user4"];
    //    console.log( data );
    //    res.end( JSON.stringify(data));
    //});
    ;
    res.send({status: 'OK',body: req.body, test: "test", headers: req.headers});
});


//app.get('/listUsers', function (req, res) {
//    fs.readFile( __dirname + "/" + "users.json", 'utf8', function (err, data) {
//        console.log( data );
//        res.end( data );
//    });
//});

var server = app.listen(8081, function () {

    var host = server.address().address
    var port = server.address().port

    console.log("BAM listening at http://%s:%s", host, port)

})