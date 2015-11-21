var http = require('http');

var options = {
  host: '127.0.0.1',
  port: '8080',
  path: '/bam/stats',
  method: 'GET'
}

var callback = function(res) {
  var body = '';
  res.on('data', function(chunk) {
    body += chunk;
  });
  res.on('end', function() {
    console.log(body);
  });
}
console.log('starting request');
http.request(options,callback);
console.log('done!');

return;
