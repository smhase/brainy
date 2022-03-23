<?php
ob_start();
session_start();

if(!isset($_SESSION['admin_id'])){
	header('Location: index.php');
}
?>
<!DOCTYPE html /> 
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.1/css/materialize.min.css">
	<link rel="stylesheet" type="text/css" href="../css/bootstrap.css" />

	<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" />
	<link rel="stylesheet" type="text/css" href="../css/admin_style.css" />
</head>
<body>

<!-- Nav Start -->
<ul id="dropdown1" class="dropdown-content" style="width:150px;">
  <li><a style="text-decoration:none;" href="train_bot.php">TrainBot</a></li>
</ul>
<nav>
  <div class="nav-wrapper black z-depth-3">
	
    <h1 class="brand-logo center">BRaiNY Dashboard</h1>
    <ul class="right hide-on-med-and-down">      
      <!-- Dropdown Trigger -->
	  <li><a href="logout.php" style="text-decoration:none;font-size:20px;" >Logout</a></li>
      <li><a style="text-decoration:none;font-size:20px;" class="dropdown-button" href="#!" data-activates="dropdown1">More <i class="fa fa-caret-down" ></i></a></li>
    </ul>
  </div>
</nav>
<!-- Nav End -->


<div class="container-fluid" >
	<div class="row" style="margin-bottom:0;">
		<div class="col-md-3 col-sm-3 col-xs-2 chats grey darken-2 z-depth-2 white-text" style="padding:10px;" ><!--Side nav column for Chats-->
			<div class="black white-text z-depth-2" style="flex:none;text-align:center;border-radius:10px;border:10px;"><h2 class="" >CHATS</h2></div>
		
			<div style="overflow-y:scroll;flex:1;" id="style-3">
				<img src="loading.gif" id="loading-sign" />
				<ul class="chats-ul">
				</ul>
			</div>
		</div>

		<div class="col-md-9 col-sm-9 col-xs-10 chatframe" ><!--Actual chat box-->
			<div class="before-chats"style="height:100%; width:100%; ">
				<!-- Welcome back message here -->
			</div>
			<div class="after-chats" style="display:none;flex-direction:column; height:100%;">
				<div class="current-chat-head cyan darken-4 white-text z-depth-3">
					<h2>Client's name here</h2>
				</div>
				<div class="current-chat-area" >
					<ul class="current-chat-ul">
						<!-- messages expanded here -->
					</ul>  
				</div>
				<div class="current-chat-footer" >
					<div class="panel-footer">
						<div class="input-group">
							<input type="text" style="margin:0px;font-size:20px;" id="message_input"/>
							<span class="input-group-btn">
								<button class="btn waves-effect waves-light" type="submit" name="action" id="consultant-button">Send</button>
							</span>
						</div>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>

<div class="chat_template"  style="display:none;"><!-- 'Chats' column template -->
	<li class="chats-item">
		<div>
			<span class="chat_name">Lokesh Mitra</span>
			<p>
				<span class="chat_text">Hi BRaiNY</span>				
			</p>
		</div>
	</li>
</div>

<div class="message_template" style="display:none;"><!-- message inside chatbox template -->
	<li class="current-chat-li">
		<div class="current-chat-message-wrapper">
			<p class="current-chat-user-message" style="font-size:15px;"></p>
			<i></i>
		</div>
	</li>
</div>

<div style="display:none;" id="hidden_admin_name"><?php echo $_SESSION['admin_id']; //JS can use php's session data from here ?></div>

<script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.1/js/materialize.min.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.7.1/firebase.js"></script>
<script type="text/javascript" src="../js/admin_script.js"></script>

</body>
</html>