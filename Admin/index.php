<?php
require 'firebaseLib.php';
ob_start();
session_start();
/*
 apiKey: "AIzaSyC9tf5j2FThWEu5srLDCkt-uTWD6elg_OI",
    authDomain: "realtimedatabase-ca7fa.firebaseapp.com",
    databaseURL: "https://realtimedatabase-ca7fa.firebaseio.com",
    storageBucket: "realtimedatabase-ca7fa.appspot.com",
    messagingSenderId: "82449120507"
*/
const DEFAULT_URL = 'https://realtimedatabase-ca7fa.firebaseio.com';
const DEFAULT_TOKEN = 'hAJhh1kxdBaoHo0uHaVnXfWoRNLmO6wRFEz2hV52'; //Database secret
const DEFAULT_PATH = '/';

$err = false;
if(!isset($_SESSION['admin_id'])){
	if(isset($_POST['username']) && isset($_POST['password'])){
		$username= explode("@", $_POST['username'])[0];
		$password = $_POST['password'];
		
		$firebase = new \Firebase\FirebaseLib(DEFAULT_URL, DEFAULT_TOKEN);
		
		$result = $firebase->get(DEFAULT_PATH . '/chat/admins/'.$username);		
		$result = json_decode($result);
		//echo $result->{'password'};
		if($result != null && $result->{'password'} == $password){
			$_SESSION['admin_id'] = $result->{'name'};
			//echo $_SESSION['admin_dept'] = $result->{'dept'};
			header('Location: admin.php');
		}else $err = true;
		
	}
}else{
	header('Location: admin.php');
}
?>
<!DOCTYPE html /> 
<html>
<head>
	<link rel="stylesheet" type="text/css" href="../css/bootstrap.css" />
<style>
body{
	background-color:#ccccff;
}
.box{
	width:400px;
	height:400px;
	background-color:white;
	border-radius:5px;
	margin:20px auto 0 auto;
	box-shadow:0 10px 20px rgba(0, 0, 0, 0.15);
	padding:20px;
}
h1{
	text-align:center;
}
.form-control{
	padding:25px 15px 25px 15px !important;
	font-size:20px !important;
	margin-top:15px;
}
.btn{
	display:block;
	width:100%;
	padding:10px;
	font-size:30px;
	background-color:#6666ff;
	color:white;
	margin-top: 15px;
}
.btn:hover, .btn:focus{
	background-color:#3232ff;
	color:white;
}
</style>
</head>
<body>
<div class="container">
<div class="box">
	<form method="post" id="myForm" action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>">
		<h1>BRaiNY Dashboard</h1>
		<hr />
		<input type="text" name="username" placeholder="Username(Email)" class="form-control" /> <!-- type="email" -->
		<input type="password" name="password" placeholder="Password" class="form-control" />
		<input type="submit" class="btn" value="Login" />
		<p id="error_show" style="display:none;color:red;">Invalid username/password</p>
	</form>
</div> <!-- End Box -->
</div>

<script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.7.1/firebase.js"></script>
<script>
var config = {
    apiKey: "AIzaSyC9tf5j2FThWEu5srLDCkt-uTWD6elg_OI",
    authDomain: "realtimedatabase-ca7fa.firebaseapp.com",
    databaseURL: "https://realtimedatabase-ca7fa.firebaseio.com",
    storageBucket: "realtimedatabase-ca7fa.appspot.com",
    messagingSenderId: "82449120507"
  };

$(document).ready(function(){	
	<?php
	if($err){
		echo '$(\'#error_show\').show();';
	}
	?>
});
</script>
</body>
</html>