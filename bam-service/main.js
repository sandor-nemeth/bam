var express = require('express')
var app = express()
var fs = require("fs")

var agents

fs.readFile( __dirname + "/" + "agents.json", 'utf8', function (err, data) {
    agents = JSON.parse(data)
})

app.get('/agent/list', function (req, res) {
  var ids = []
  for(id in agents){
    ids.push(id)
  }
  
   res.end( JSON.stringify(ids) )
})

app.get('/agent/add/:id/:host/:port', function (req, res) {
  var id = req.params.id
  var host = req.params.host
  var port = req.params.port
  
  var newAgent = {
    "id":id,
    "host":host,
    "port":port
  }
  
  agents[id] = newAgent
  console.log("add agent " + id)
  res.end(JSON.stringify({"status":"ok","newAgent":newAgent}))
  saveAgents()
})

app.get('/agent/detail/:id', function (req, res) {
  var agent = agents[req.params.id]
   res.end(JSON.stringify(agent))
})

app.get('/agent/delete/:id', function (req, res) {
  var id = req.params.id
  var agentToDelete = agents[id]
  delete agents[id]
  
  console.log("delete agent " + id)
   res.end(JSON.stringify({"status":"ok","deletedAgend":agentToDelete}))
   
   saveAgents()
})

var server = app.listen(8081, function () {

  var host = server.address().address
  var port = server.address().port

  console.log("Example app listening at http://%s:%s", host, port)
})

function saveAgents(){
 fs.readFile( __dirname + "/" + "agents.json", 'utf8', function (err, data) {
    agents = JSON.parse(data)
  })
  
  fs.writeFile(__dirname + "/" + "agents.json", JSON.stringify(agents),  function(err) {
   if (err) {
       return console.error(err);
   }   
  }) 
}