var express = require('express')
var app = express()
var fs = require("fs")

var agents

fs.readFile( __dirname + "/" + "agents.json", 'utf8', function (err, data) {
    agents = JSON.parse(data)
})

app.get('/agent', function (req, res) {
  var ids = []
  for(id in agents){
    ids.push(id)
  }
  
   res.end( JSON.stringify(ids) )
})

app.put('/agent', function (req, res) {  
  var newAgent = "";
  
  agents[newAgent] = newAgent
  console.log("add agent " + newAgent)
  res.end(JSON.stringify({"status":"ok","newAgent":newAgent}))
  saveAgents()
})

app.delete('/agent', function (req, res) {
  var agentToDelete = ""
  delete agents[agentToDelete]
  
  console.log("delete agent " + agentToDelete)
   res.end(JSON.stringify({"status":"ok","agentToDelete":agentToDelete}))
   
   saveAgents()
})

app.get("/stats", function(req,res){
  var stats = {
    "numberOfExecutions":0,
    "numberOfItemsProcessed":0,
    "totalExecutionTime":0
  }
  
  res.end(JSON.stringify(stats))
})

app.get("/jobs", function(req,res){
  var jobs = []
  
  res.end(JSON.stringify(jobs))
})

var server = app.listen(3000, function () {

  var host = server.address().address
  var port = server.address().port

  console.log("Example app listening at http://%s:%s", host, port)
})

function saveAgents(){  
  fs.writeFile(__dirname + "/" + "agents.json", JSON.stringify(agents),  function(err) {
   if (err) {
       return console.error(err);
   }   
  }) 
}