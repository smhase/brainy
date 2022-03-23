<?php
ob_start();
session_start();

if(!isset($_SESSION['admin_id'])){
	header('Location: index.php');
	//echo $_SESSION['admin_dept'];
}
?>
<!DOCTYPE html /> 
<html>
<head>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.1/css/materialize.min.css">
	<!-- <link rel="stylesheet" type="text/css" href="bootstrap.css" /> -->

	<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" />
	<link rel="stylesheet" type="text/css" href="../css/admin_style.css" />
	<style>
	.row{
		height:80vh;
	}
	</style>
</head>
<body>
<nav>
    <div class="nav-wrapper black">
	<a class="fa fa-arrow-left fa-3x" href="admin.php" style="padding:10px;"></a>
	<h4 class="brand-logo">Train BRaiNY</h4>
	</div>
  </nav>

<!--
<div class="container" style="padding:10px">
	<div class="hoverable" style="border:1px solid #bdbdbd; width:100%;">
		<button>asdsd</button>
	</div>
</div> -->
<div class="row" >
    <div class="col s12">
      <ul class="tabs">
        <li class="tab col s6"><a class="active" href="#test1">Add a question</a></li>
        <li class="tab col s6"><a href="#test2">Update a question</a></li>
        
    
      </ul>
    </div>
    <div id="test1" class="col s12">
	<form style="padding:30px;" id="new_question_form">
		<h4>Teach BRaiNY a new question</h4>
		<div class="input-field">
          <input placeholder="Enter the question here" id="question_new" type="text" class="validate">
          <label for="first_name">Question</label>
        </div>
        <div class="input-field">
          <input placeholder="Enter the answer here" id="answer_new" type="text" class="validate">
          <label for="last_name">Answer</label>
        </div>
		<button class="btn waves-effect waves-light" type="submit" name="action">Submit</button>
	</form>
	</div>
    <div id="test2" class="col s12" >
	<form style="padding:30px;" id="update_question_form">
		<h4>Change a question</h4>
		<div class="row">
			<div class="col s6">
				<div class="them_questions" style="border:1px solid #bdbdbd;height:300px;overflow-y:scroll;">
					<ul class="collection">
					  <!--
					  <li class="collection-item"><span id="q">Question 1</span><hr /><span id="a">Answer 1</span></li>
					  <li class="collection-item"><span id="q">Question 2</span><hr /><span id="a">Answer 2</span></li>
					  <li class="collection-item"><span id="q">Question 3</span><hr /><span id="a">Answer 3</span></li>
					  <li class="collection-item"><span id="q">Question 4</span><hr /><span id="a">Answer 4</span></li>
					  <li class="collection-item"><span id="q">Question 5</span><hr /><span id="a">Answer 5</span></li>
					  -->
					</ul>
				</div>
			</div>
			<div class="col s6 update-fields">
				<div class="input-field">
				  <input placeholder="Question" id="question" type="text"  />
				</div>
				<div class="input-field">
				  <input placeholder="Answer" id="answer" type="text"/>
				</div>
				<button class="btn waves-effect waves-light" type="submit" name="action">Submit</button>
			</div>
		</div>
		
	</form>
	</div>
    
  </div>
  <div  style="display:none;" class="qa_temp">
		 <li class="collection-item" style="cursor:pointer"><span id="q">Question 5</span><hr /><span id="a">Answer 5</span></li>
	</div>
<script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.1/js/materialize.min.js"></script>
<script src="https://www.gstatic.com/firebasejs/3.7.1/firebase.js"></script>

<script type="text/javascript">
$(document).ready(function(){
	$.ajax({//getting questions
			url: "http://localhost:8080/Startup/DisplayAll",				//<-PRASHANT
			type : 'GET',
			data : {  },
			success : function(data){
				console.log(data);
				if(data!= null  && data != ""){
				var jj = JSON.parse(data);
				for(var j in jj){
					var qa = jj[j];
					var message = $($('.qa_temp').clone().html());
					message.find('#q').text(qa.question);
					message.find('#a').text(qa.answer);
					$('.collection').append(message);
				}
				}
			},
			error : function(ts){
				console.log(ts);
				//form_status.html('<p class="text-success">Page Error </p>').delay(3000).fadeOut();
			}
		});
	$('#new_question_form').submit(function(e){
		e.preventDefault();
	});
	$('.collection').on('click', '.collection-item', function(){
		//console.log($(this).find('#q').text());
		var que = $(this).find('#q').text();
		var ans = $(this).find('#a').text();
		$('.update-fields').find('#question').val(que);
		$('.update-fields').find('#answer').val(ans);
	});
	
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
	

});
</script>

</body>
</html>