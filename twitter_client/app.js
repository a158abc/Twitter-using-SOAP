var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');

var routes = require('./routes/index');
var users = require('./routes/users');
var home = require('./routes/home');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(session({
	  secret: 'knhdbfvjkhbk2365791259bvkjbjpok',
	  saveUninitialized :true,
	  resave : true// set this to a long random string!
	}));
app.use(express.static(path.join(__dirname, 'public')));

app.get('/', home.signup);
app.post('/afterSignUp',home.afterSignUp);

app.get('/addUserName', home.addUserName);
app.post('/updateUserName',home.updateUserName);

app.get('/signin', home.signin); 
app.post('/afterSignIn',home.afterSignIn);

app.get('/home',home.homepage);
app.get('/onLoadData',home.onLoadData);
app.post('/insertTweet',home.insertTweet);
app.get('/profile',home.profile);
app.post('/searchHash',home.searchHash);
app.get('/getProfile',home.getProfile);
app.post('/follow',home.follow);
app.post('/addBio', home.addBio);
app.post('/logout',home.logout);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
