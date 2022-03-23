<?php
if(isset($_GET['questionSet']))
    echo "{\"answer\":\"Bot says - ".$_GET['questionSet']."\"}";
?>