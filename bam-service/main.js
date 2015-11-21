var express = require('express')
var bodyParser = require('body-parser')
var request = require('request');
var app = express()
var fs = require("fs")

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

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
  var newAgent = req.body
  var newAgentId = agentToId(newAgent)

  agents[newAgentId] = newAgent
  console.log("add agent " + newAgentId)
  res.end(JSON.stringify({"status":"ok","newAgent":newAgent}))
  saveAgents()
})

app.delete('/agent/:host/:port', function (req, res) {
  var agentToDelete = {
    "host":req.params.host,
    "port":req.params.port
  }

  var agentToDeleteId = agentToId(agentToDelete)
  delete agents[agentToDeleteId]

  console.log("delete agent " + agentToDeleteId)
   res.end(JSON.stringify({"status":"ok","agentToDelete":agentToDelete}))

   saveAgents()
})

app.get("/stats", function(req,res){
  var stats = {
    "numberOfExecutions":0,
    "numberOfItemsProcessed":0,
    "totalExecutionTime":0
  }
  var handler = function(body) {
    stats.numberOfExecutions = body.numberOfExecutions
    stats.numberOfItemsProcessed = body.numberOfItemsProcessed
    stats.totalExecutionTime = body.totalExecutionTime
  }
  var callback = function() {
    res.end(JSON.stringify(stats))
  }

  callAgents("/bam/stats", handler, callback);

})

app.get("/jobs", function(req,res){
  var jobs = []

  var i = 0

  for(agentId in agents){
    request("http://" + agentId + "/bam/jobs", function (error, response, body) {
      i++

      if (!error && response.statusCode == 200) {
        jobs[jobs.length] = body
      }else{
        console.log(error + " " + response + " " + body)
      }

      if( i == agents.length){
          res.end(JSON.stringify(jobs))
      }
    })
  }
})

callAgents = function(target, handler, callback) {
  var i = 0;
  for (agentId in agents) {
    if (agentId === "undefined:undefined") { ++i; continue; }
    console.log("Trying agent: " + agentId);
    request("http://" + agentId + target, function(error, response, body) {
      ++i;
      if (!error && response.statusCode == 200) {
        handler(body)
      } else {
        console.log(error + " " + response + " " + body);
      }
      if (i = agents.length - 1) {
        callback()
      }
    });
  }
}

var server = app.listen(4000, function () {

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

function agentToId(agent){
  return agent.host + ":" + agent.port
}
