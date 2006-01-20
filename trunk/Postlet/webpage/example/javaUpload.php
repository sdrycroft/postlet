<?php
/*
The following file enables the uploading of each image from the java applet.
*/

$uploaddir = '/home/groups/p/po/postlet/uploads/';

$cmd = 'touch /home/groups/p/po/postlet/upload';
exec($cmd);
echo $cmd;

$fpath = $_FILES['userfile']['name'];
$fext = array_pop(explode('.', $fpath));
$ip = getip();
if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploaddir .$ip.'-'. $fpath))
{
    echo "YES";
} 
else
{
    echo "NO";
	print_r($_FILES); 
} 

function getip() {
   if (getenv("HTTP_CLIENT_IP") && strcasecmp(getenv("HTTP_CLIENT_IP"), "unknown"))
   $ip = getenv("HTTP_CLIENT_IP");

   else if (getenv("HTTP_X_FORWARDED_FOR") && strcasecmp(getenv("HTTP_X_FORWARDED_FOR"), "unknown"))
   $ip = getenv("HTTP_X_FORWARDED_FOR");

   else if (getenv("REMOTE_ADDR") && strcasecmp(getenv("REMOTE_ADDR"), "unknown"))
   $ip = getenv("REMOTE_ADDR");

   else if (isset($_SERVER['REMOTE_ADDR']) && $_SERVER['REMOTE_ADDR'] && strcasecmp($_SERVER['REMOTE_ADDR'], "unknown"))
   $ip = $_SERVER['REMOTE_ADDR'];

   else
   $ip = "unknown";

   return $ip;
}
?>