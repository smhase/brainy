var config = {
    apiKey: "AIzaSyC9tf5j2FThWEu5srLDCkt-uTWD6elg_OI",
    authDomain: "realtimedatabase-ca7fa.firebaseapp.com",
    databaseURL: "https://realtimedatabase-ca7fa.firebaseio.com",
    storageBucket: "realtimedatabase-ca7fa.appspot.com",
    messagingSenderId: "82449120507"
  };
	
var sendMessage;
var userRef;

var myAdminName;
var myAdminDept;

var myOnce = true;
		
var NewChat = function (arg) {//Adds into the "Chats" side nav column
	this.username = arg.username; this.message = arg.message; this.from_bot = arg.from_bot;this.timestamp = arg.timestamp;
	this.name = arg.name;this.admin_name = arg.admin_name;this.show_new = arg.show_new;
	this.draw = function (_this) {
		return function () {
			var $chat;
			$chat = $($('.chat_template').clone().html());
			$chat.find('.chat_name').html(_this.name);
			if(_this.show_new)
				$chat.find('.chat_name').addClass('new-chat');
			if(_this.message.length > 25) _this.message =  _this.message.slice(0,25)+"...";
				$chat.find('.chat_text').html(_this.message);
			$chat.attr("data-username",_this.username);//data-username used when admin clicks on this
			
			$chat.data("data-timestamp",_this.timestamp);
			$chat.data("data-name",_this.name);
			
			$('.chats-ul').prepend($chat);
		};
	}(this);
	return this;
};

var Message = function (arg) {//Adds message into current chatting area
	this.text = arg.text, this.message_side = arg.message_side; this.timestamp = arg.timestamp;
	this.draw = function (_this) {
		return function () {
			var $message, $messages;
			$messages = $('.current-chat-ul');
			$message = $($('.message_template').clone().html());
			$message.find('p').html(_this.text);
			var newDate = new Date(_this.timestamp);
			$message.find('i').html(newDate.getHours()+":"+newDate.getMinutes());
		
			if(myOnce == true){
				myOnce = false;
				$messages.append("<li style=\"color:white;text-align:center;border-top:1px solid white; border-bottom:1px solid white\">"+newDate.toDateString()+"</li>")//Date at top of chatobx
			}
			
			if(new Date($messages.children().last().data("data-timestamp")).getDate() < newDate.getDate()){				
				$message.prepend("<li style=\"color:white;text-align:center;border-top:1px solid white; border-bottom:1px solid white\">"+newDate.toDateString()+"</li>");//Date whenever it changes
			}
			
			$message.data("data-timestamp",_this.timestamp);
			
			
			if(!_this.message_side){ 
				$message.addClass('left').addClass('bg-green');
			}else {
				$message.addClass('right').addClass('bg-blue');				
			}
			
			$messages.append($message);
			$('.current-chat-area').animate({ scrollTop: $('.current-chat-area').prop('scrollHeight') }, 30);//move to focus
		};
	}(this);
	return this;
};


$(document).ready(function(){
	firebase.initializeApp(config);   //Firebase started
	const dbRef = firebase.database().ref().child('chat');
	var messagesRef = dbRef.child('messages');
	const usersRef = dbRef.child('users');
	
	var load = $('#loading-sign');
	myAdminName = $('#hidden_admin_name').text();
	$('.before-chats').html("<p style=\"text-align:center; font-size:30px; font-weight:bold; padding:50px\">Welcome back, "+myAdminName+"</p>");
	var theNames = {};
	
	usersRef.on('value', function(data){//Adding new users' names to the 'theNames' array
		for(var mNames in data.val()){
			var obj = data.val()[mNames];
			theNames[mNames] = obj.name;
		}
	});
	messagesRef.once('value').then(function(data){//For init chats side nav column
		var allChats = data.val();
		//var allChatsUsers = Object.keys(allChats);//array of usernames

		for(var allChatsUsers in allChats){
			var userMessagesObject = allChats[allChatsUsers];
			var lastMessage;
			for(var userMessage in userMessagesObject);
			lastMessage = userMessagesObject[userMessage];

			new NewChat({
				username: allChatsUsers,//username
				name:theNames[allChatsUsers],//actual name
				message: lastMessage.message,
				from_bot : lastMessage.key_from_bot,
				timestamp : lastMessage.timestamp,
				show_new : false
			}).draw();
		}
		load.hide();
		messagesRef.on('value', function(database){//For updates in chats side nav column
			var chats = database.val();
			for(var chat in chats){
				var userChatObject = chats[chat];
				for(var mes in userChatObject);
				var myMes = userChatObject[mes];//get the last message
				
				var oneChat = $('.chats-ul').find('[data-username = '+chat+']');
				
				if(oneChat.length){ //if user already is in Chats column
					if(oneChat.data("data-timestamp") < myMes.timestamp){//check if it is new message							
						if(myMes.message.length > 25) 
							myMes.message =  myMes.message.substring(0,25)+"...";
						oneChat.find('.chat_text').text(myMes.message);
						if(typeof $('.current-chat-head').data("data-username") == 'undefined'
							|| $('.current-chat-head').data("data-username") != chat)
							oneChat.find('.chat_name').css({'color':'green', 'font-weight':'bold'});
						oneChat.data("data-timestamp", myMes.timestamp);
						$('.chats-ul').prepend(oneChat);
					}
				}else{//new user
					new NewChat({
						username: chat,
						name:theNames[chat],
						message: myMes.message,
						from_bot : myMes.key_from_bot,
						timestamp : myMes.timestamp,
						show_new : true
					}).draw();
				}
				
				//add new messages if chatbox open for that user
				if($('.current-chat-head').data("data-username") == chat && 
					$('.current-chat-ul').children('.current-chat-li').last().data("data-timestamp") < myMes.timestamp){
						new Message({
							text : myMes.message,
							message_side : myMes.key_from_bot,
							//admin_name : myMes.admin_name,
							timestamp : myMes.timestamp
						}).draw();
				}
				
			}
		});
		
	});
		
	sendMessage = function (text) {
		if (text.trim() === '') {
			return;
		}
		$('#message_input').val('');
		
		var pushMessage = {
			key_from_bot : true,
			message : text,
			timestamp : new Date().getTime()
		};
		messagesRef.child($('.current-chat-head').data("data-username")).push(pushMessage);	
	}
	
	$('.chats-ul').on('click', '.chats-item', function(){//When item from 'Chats' is clicked
		$('.chats-item').removeClass('selected');
		$(this).addClass('selected');
		$(this).find('.chat_name').css({'color':'', 'font-weight':''});
		$('.before-chats').hide();
		$('.after-chats').css("display","flex");
		$('.current-chat-area').show();
		var uname = $(this).attr("data-username");
		var mName = $(this).data("data-name");
		
		$('.current-chat-head').find('h2').text(mName);
		$('.current-chat-head').data("data-username", uname);
		$('.current-chat-ul').empty();
		
		if(myOnce == false) myOnce = true;
		
		
		userRef = messagesRef.child(uname);
		userRef.once('value').then(function(datab){//fetching user's all messages
			var messageKeys = datab.val();
			for(var messageKey in messageKeys){
				var msg = messageKeys[messageKey];
				new Message({
					text : msg.message,
					message_side : msg.key_from_bot,
					timestamp : msg.timestamp
				}).draw();
			}
		});
		
	});
	
	
	$('#consultant-button').click(function () {
		console.log("clicked");
		return sendMessage($('#message_input').val());
	});
	$('#message_input').keyup(function (e) {
		if (e.which === 13) {
			return sendMessage($(this).val());
		}
	});
	$('.dropdown-button').dropdown({//Materilaize 
		inDuration: 300,
		outDuration: 225,
		constrain_width: false, // Does not change width of dropdown to that of the activator
		gutter: 0, // Spacing from edge
		belowOrigin: false // Displays dropdown below the button
		}
	);
	
});
