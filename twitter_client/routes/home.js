var ejs = require("ejs");
var crypto = require("crypto");
var session = require("express-session");
var soap = require('soap');
var baseURL = "http://localhost:8080/twitter3/services";

function signup(req,res) 
{
	console.log("Displaying signup page");
	res.render('signup');
}

function afterSignUp(req,res)
{
	console.log("inside after signup");
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	var cipher = crypto.createCipher('aes-256-ctr', 'd6F3Efeqwerty');
	var hash = cipher.update(req.param("inputPassword"),'utf8','hex');
	hash += cipher.final('hex');
	var args = {inputEmail: req.param('inputEmail'),inputFirstname: req.param('inputFirstname'),inputLastname: req.param('inputLastname'),hash: hash};
  	console.log("initialising SOAP call in aftersignup");
  	soap.createClient(url,option, function(err, client) 
	{
      	client.signup(args, function(err, result) 
    	{
    	  if(result.signupReturn === true)
    	  {
    		  req.session.email = req.param('inputEmail');
    		  res.send({statusCode:200});
    	  }
    	  else
    	  {
    		  res.send({statusCode:401});
    	  }
      });
  });
}

function addUserName(req, res)
{
	console.log("displaying Email page");
	res.render("username");
}

function updateUserName(req, res)
{
	console.log("inside updateUserName function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {inputUsername: req.param('inputUsername'),inputEmail: req.session.email};
  	console.log("initialising SOAP call in update UserName");
  	soap.createClient(url,option, function(err, client) 
	{
      	client.username(args, function(err, result) 
    	{
    	  if(result.usernameReturn === true)
    	  {
    		  console.log("username inserted successfully");
    		  res.send({statusCode:200});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });
}

function signin(req,res) 
{
	console.log("Displaying signin page");
	res.render('signin',function(err, result) 
	{
	   if (!err) 
	   {
	            res.end(result);
	   }
	   else 
	   {
	            res.end('An error occurred');
	            console.log(err);
	   }
   });
}

function afterSignIn(req,res)
{
	console.log("Inside the afterSignIn function");
	var decipher = crypto.createDecipher('aes-256-ctr', 'd6F3Efeqwerty')
	var hash = decipher.update(req.param("inputPassword"),'utf8','hex')
	hash += decipher.final('hex');
	// check user already exists
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {inputEmail: req.param('inputEmail'),inputPassword:hash };
  	console.log("initialising SOAP call in signin UserName");
  	soap.createClient(url,option, function(err, client) 
	{
      	client.signin(args, function(err, result) 
    	{
    	  if(result.signinReturn === true)
    	  {
    		  console.log("user logged in successfully");
    		  req.session.Email=req.param("inputEmail");
    		  res.send({statusCode:200});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });	
}


function homepage(req,res)
{
	res.render("twitter_home");
}

function onLoadData(req,res)
{
	console.log("inside onLoadData function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {inputEmail: req.session.Email};
  	console.log("initialising SOAP call in onLoad Data");
  	soap.createClient(url,option, function(err, client) 
	{
  		console.log(args);
      	client.onLoadData(args, function(err, result) 
    	{
      		console.log("result is: "+result.onLoadDataReturn);
    	  if(result.onLoadDataReturn)
    	  {
    		  // make a new json object with each new line
    		  var allTweets = result.onLoadDataReturn;
    		  var arr = allTweets.split("\n");
    		  console.log(arr);
    		  res.send({tweets: arr[0], userInfo: arr[1], following: arr[2], followers: arr[3], numTweets:arr[4],suggestedUsers:arr[5]});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });
}


function insertTweet(req, res)
{
	
console.log("inside insertTweets function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	var time = new Date();
	
	var args = {Email: req.session.Email, Tweet : req.body.tweet, Time: time};
  	console.log("initialising SOAP call in insert Tweet Data");
  	soap.createClient(url,option, function(err, client) 
	{
  		console.log(args);
      	client.insertTweet(args, function(err, result) 
    	{
      		console.log("result is: "+result.insertTweetReturn);
    	  if(result.insertTweetReturn === true)
    	  {
    		  // make a new json object with each new line
    		  console.log("tweets inserted successfully");
    		  res.render('twitter_home', function(err, result){
  				if(!err){
  					res.end(result);
  					}
  						
  				else{
  					res.end("Error");
  					console.log(err);
  				}
  			});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });
}


function profile(req, res){
	
	if(req.session.Email){
		res.render("profile");
	}
}

function getProfile(req,res){
	console.log("inside profile function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {inputEmail: req.session.Email};
  	console.log("initialising SOAP call in getProfile Data");
  	soap.createClient(url,option, function(err, client) 
	{
  		console.log(args);
      	client.getProfile(args, function(err, result) 
    	{
      		console.log("result is: "+result.getProfileReturn);
    	  if(result.getProfileReturn)
    	  {
    		  // make a new json object with each new line
    		  var allTweets = result.getProfileReturn;
    		  var arr = allTweets.split("\n");
    		  console.log(arr);
    		  res.send({tweets: arr[0], userInfo: arr[1], following: arr[2], followers: arr[3], numTweets:arr[4], followingList:arr[5], followersList:arr[6],suggestedUsers:arr[7]});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });
}

function searchHash(req,res){
	
	
console.log("inside profile function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	
	var Hash = req.param("Hash");
	var simplestr = Hash.substring(1, Hash.length);
	console.log(simplestr);
	
	var args = {simplestr: simplestr};
  	console.log("initialising SOAP call in search Hash");
  	soap.createClient(url,option, function(err, client) 
	{
  		console.log(args);
      	client.searchHash(args, function(err, result) 
    	{
      		console.log("result is: "+result.getsearchHashReturn);
    	  if(result.searchHashReturn)
    	  {
    		  res.send({searchTweets: result.searchHashReturn});
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send({statusCode:401});
    	  }
      });
  });
}

function follow(req, res){
	console.log("inside follow function");
	
	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {inputEmail: req.session.Email, followEmail:req.param("Email")};
  	console.log("initialising SOAP call in follow");
  	soap.createClient(url,option, function(err, client) 
	{
      	client.follow(args, function(err, result) 
    	{
    	  if(result.followReturn === true)
    	  {
    		  console.log("follow inserted successfully");
    		  res.send("Inserted!");
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send("error occured");
    	  }
      });
  });
}

function addBio(req,res){
	
	
	//var insertBio = "UPDATE Account SET location= '"+req.param("location")+"', dob = '"+req.param("dob")+"', phone = '"+req.param("phone")+"'  WHERE Email = '"+req.session.Email+"'";

	var option = {ignoredNamespaces : true};
	var url = baseURL+"/HomeJava?wsdl";
	
	var args = {location : req.param("location"), dob : req.param("dob"), phone: req.param("phone"), inputEmail: req.session.Email};
	
  	console.log("initialising SOAP call in addBio");
  	soap.createClient(url,option, function(err, client) 
	{
      	client.addBio(args, function(err, result) 
    	{
    	  if(result.addBioReturn === true)
    	  {
    		  console.log("addBio inserted successfully");
    		  res.redirect('/profile');
    	  }
    	  else
    	  {	
    		  console.log("error occured");
    		  res.send("error occured");
    	  }
      });
  });
}

function logout(req, res){
	req.session.destroy();
	res.send("loged out!");
}

exports.signup = signup;
exports.afterSignUp=afterSignUp;
exports.addUserName = addUserName;
exports.updateUserName = updateUserName;
exports.signin = signin;
exports.afterSignIn = afterSignIn;
exports.homepage=homepage;
exports.onLoadData=onLoadData;
exports.insertTweet=insertTweet;
exports.profile = profile;
exports.getProfile=getProfile;
exports.searchHash=searchHash;
exports.follow=follow;
exports.addBio=addBio;
exports.logout=logout;