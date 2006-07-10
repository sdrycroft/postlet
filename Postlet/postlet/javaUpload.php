<?php
/*
The following file enables the uploading of each image from the java applet.

PLEASE NOTE, THIS FILES IN ITS PRESENT FORM IS A MASSIVE SECURITY RISK, AND
SHOULD NOT BE USED WITHOUT DOING EITHER OF THE FOLLOWING:

- PROTECTING THE ACCESS OF THE FILE BY THE USE OF SESSION VARIABLES (DO NOT
  PROTECT IT BY USING HTTP PASSWORDS)
- ENSURING THAT UPLOADED FILES ARE NOT ACCESSIBLE TO THE WEB (UPLOAD FILES
  ABOVE THE DOCUMENT ROOT)
*/

echo "RECEIVING:";

$uploaddir = '[PATH TO UPLOAD DIRECTORY]';

$fpath = $_FILES['userfile']['name'];
$fext = array_pop(explode('.', $fpath));

if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploaddir . $fpath))
{
    echo "YES";
} 
else
{
    echo "NO";
	print_r($_FILES); 
} 

?>
