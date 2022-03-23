var sendMessage, sendMessageBot;

var testCounter=0;

var config = {    //CONFIG KEY FOR FIREBASE
    apiKey: "AIzaSyC9tf5j2FThWEu5srLDCkt-uTWD6elg_OI",
    authDomain: "realtimedatabase-ca7fa.firebaseapp.com",
    databaseURL: "https://realtimedatabase-ca7fa.firebaseio.com",
    storageBucket: "realtimedatabase-ca7fa.appspot.com",
    messagingSenderId: "82449120507"
  };

var firstLoad = true;

var cant = false;
var cantq, canta;

function setCookie(cname, cvalue) {
    document.cookie = cname + "=" + cvalue + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

$(document).ready(function(){
	firebase.initializeApp(config);   //Firebase started
		
	var dbRef = firebase.database().ref().child('chat');
	var messagesRef = dbRef.child('messages');
	var usersRef = dbRef.child('users');
	var allMessagesRef;//	= messagesRef.child(username);
	//Firebase
	
	
	var loginUser = function(name, email){
			var uname = email.split("@")[0];
			setCookie("name", name);
			setCookie("email", email);
			usersRef.child(uname).set({
				email : email,
				name : name
			},function(){
				console.log(uname);
				$('.bottom_wrapper').fadeIn();
				$('.userDetails').hide();
				allMessagesRef = messagesRef.child(uname);

				allMessagesRef.once('value').then(function(snap){
					console.log("Called once");
					if(snap.val() != null){
						var allMessages = snap.val();
						var allMessagesArr = Object.keys(allMessages);
						allMessagesArr = allMessagesArr.slice(0, allMessagesArr.length);
						for(var i = 0; i< allMessagesArr.length; i++){	
							
							var m = allMessages[allMessagesArr[i]];
							if(m.key_from_bot){
								
								new Message({
									text: m.message,
									message_side: 'left'
									}).draw();			
								
							}else{
								new Message({
									text: m.message,
									message_side: 'right'
									}).draw();
							}
						}
					}
					allMessagesRef.limitToLast(1).on('value', function(data){
						console.log("Called on");
						var allMessages = data.val();
						for(var i in allMessages){
							var m = allMessages[i];
							if(!firstLoad){
							if(m.key_from_bot){						
								new Message({
									text: m.message,
									message_side: 'left'
									}).draw();
								if(cant == true){
									console.log("ZZZ");
									canta=m.message;
									var new_ques = JSON.stringify({
										question : cantq,
										answer: canta
									});
									$.ajax({
										url: "http://localhost:8080/Startup/StanfordAdmin",				//<-PRASHANT
										type : 'GET',
										data : { questionSet : new_ques},
										success : function(data){
											console.log(data);
											//if(data!= null  && data != ""){
										//		alert(data);
											//}
											cant=false;
										},
										error : function(ts){
											console.log(ts);
											//form_status.html('<p class="text-success">Page Error </p>').delay(3000).fadeOut();
										}
									});
									
								}
								/*
								$('#ting')[0].play();
								if($('.chatCircle').css('display') == 'none' ){
									$('.chatCircle').find('i').removeClass('hide');
								}
								*/
							}else{					
								new Message({
									text: m.message,
									message_side: 'right'
									}).draw();
							}
							}
						}
						firstLoad = false;
					});
				});
			
			
		
			});
		}
		
	var cookieName = getCookie("name");
	var cookieEmail = getCookie("email");
    if (cookieName != "" && cookieEmail != "") {
		loginUser(cookieName, cookieEmail);
    } 
    var Message;
    Message = function (arg) {
        this.text = arg.text, this.message_side = arg.message_side;
        this.draw = function (_this) {
            return function () {
                var $message;
                $message = $($('.message_template').clone().html());
                $message.addClass(_this.message_side).find('.text').html(_this.text);
                $('.messages').append($message);
                $('.messages').animate({ scrollTop: $('.messages').prop('scrollHeight') }, 30);
                return $message.addClass('appeared');
            };
        }(this);
        return this;
    };
    
	var getMessageText;
	getMessageText = function () {
		var $message_input;
		$message_input = $('.message_input');
		return $message_input.val();
	};
	sendMessage = function (text) {
		var $messages, message;
		if (text.trim() === '') {
			return;
		}
		$('.message_input').val('');
		$messages = $('.messages');
		
		var testMessage = {
			key_from_bot : false,
			message : text,
			timestamp : new Date().getTime()
		};
		allMessagesRef.push(testMessage);//Sending the message to FireBase
		
		/*
		 * AJAX!!
		 */
		$.ajax({
			url: "http://localhost:8080/Startup/StanServlet",			
			type : 'GET',
			data : {questionSet:text},
			success : function(data){
				console.log(data);
				if(data.length)
				allMessagesRef.push({
					key_from_bot : true,
					message : JSON.parse(data).answer+" | Score:"+JSON.parse(data).score+"%",
					timestamp : new Date().getTime()
				});
				else{
					allMessagesRef.push({
						key_from_bot : true,
						message : "Can't understand that",
						timestamp : new Date().getTime()
					});
					console.log("XX");
					cant = true;
					cantq = text;
				}

			},
			error : function(ts){
				console.log(ts);
			}
		});
		
		
	};
	
	$('.send_message').click(function (e) {
		return sendMessage(getMessageText());
		
	});
	$('.message_input').keyup(function (e) {
		if (e.which === 13) {
			return sendMessage(getMessageText());
		}
	});

	$('.chatCircle').click(function(){
		$(this).find('i').addClass('hide');
		$(this).fadeOut(300);
		$('.chat_window').fadeIn(300).animate({ height: 500, opacity: 1 }, 'slow');
	});

	$('#closeButton').click(function(){
		$('.chat_window').animate({ height: 0 }, 'slow', function(){
			$('.chatCircle').fadeIn(300);
		});
	});
	$('.userDetailsForm').submit(function(event){
		event.preventDefault();
		var name,email, uname;
		name = $('#details_name').val();
		email = $('#details_email').val();
		loginUser(name, email);
		
	});
	/*
		$('#new_question_form').submit(function(e){
		e.preventDefault();
		var new_ques = JSON.stringify({
			question : $('#question_new').val(),
			answer: $('#answer_new').val()
		});
	$('#answer_new').val("");
	$('#question_new').val("");
	
		$.ajax({
			url: "http://localhost:8080/Startup/StanfordAdmin",				//<-PRASHANT
			type : 'GET',
			data : { questionSet : new_ques},
			success : function(data){
				console.log(data);
				if(data!= null  && data != ""){
					alert(data);
				}
			},
			error : function(ts){
				console.log(ts);
				//form_status.html('<p class="text-success">Page Error </p>').delay(3000).fadeOut();
			}
		});
	});
	*/
});