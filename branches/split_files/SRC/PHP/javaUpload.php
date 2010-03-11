<?php
/*
The following file enables the uploading of each image from the java applet.

PLEASE NOTE, THIS FILES IN ITS PRESENT FORM IS A MASSIVE SECURITY RISK, AND
SHOULD NOT BE USED WITHOUT DOING EITHER OF THE FOLLOWING:

- PROTECTING THE ACCESS OF THE FILE BY THE USE OF SESSION VARIABLES (DO NOT
  PROTECT IT BY USING HTTP PASSWORDS)
- ENSURING THAT UPLOADED FILES ARE NOT ACCESSIBLE TO THE WEB (UPLOAD FILES
  TO A DIRECTORY ABOVE THE DOCUMENT ROOT)
*/

/*
AS OF POSTLET 0.11, POSTLET READS THE MESSAGES SENT BACK TO IT, AND NOW
IS AWARE OF WHETHER OR NOT AN UPLOAD HAS BEEN SUCCESSFUL. MORE INFORMATION
ABOUT THE FORMAT OF REPLY MESSAGES CAN BE FOUND ON THE POSTLET WEBSITE 
http://www.postlet.com/install/
*/

// Configuration ---------------------------------------------------------------
// Change the below path to the folder where you would like files uploading.
// e.g. "/home/yourname/myuploads/"
// or "c:\php\uploads\"
// Note, this MUST have the trailing slash.
$uploaddir = dirname(__FILE__) . "/uploads/";
$partsdir  = dirname(__FILE__) . "/parts/";
// Whether or not to allow the upload of specific files
$allow_or_deny = true;
// If the above is true, then this states whether the array of files is a list of
// extensions to ALLOW, or DENY
$allow_or_deny_method = "deny"; // "allow" or "deny"
$file_extension_list = array("php","asp","pl");
// -----------------------------------------------------------------------------
if ($allow_or_deny){
	if (($allow_or_deny_method == "allow" && !in_array(strtolower(array_pop(explode('.', $_FILES['userfile']['name']))), $file_extension_list))
		|| ($allow_or_deny_method == "deny" && in_array(strtolower(array_pop(explode('.', $_FILES['userfile']['name']))), $file_extension_list))){		
		// Atempt to upload a file with a specific extension when NOT allowed.
		// 403 error
		header("HTTP/1.1 403 Forbidden");
		echo "POSTLET REPLY\r\n";
		echo "POSTLET:NO\r\n";
		echo "POSTLET:FILE TYPE NOT ALLOWED\r\n";
		echo "POSTLET:ABORT THIS\r\n"; // Postlet should NOT send this file again.
		echo "END POSTLET REPLY\r\n";
		exit;
	}
}

file_put_contents(dirname(__FILE__) . "/debug.log", print_r($_POST, true));

// Move it to parts first
$sName = $_FILES['userfile']['name'];
$sFile = $sName . ".part" . $_POST['chunk'];
if (move_uploaded_file($_FILES['userfile']['tmp_name'], $partsdir . $sFile))
{
	// It was succesful, check the session to see if we have a complete file
	session_name('postlet');
	session_start();

	if(!isset($_SESSION['fileparts']))
		$_SESSION['fileparts'] = Array();

	// Check for this file
	if(!isset($_SESSION['fileparts'][$sName]))
		$_SESSION['fileparts'][$sName] = 1; // 1 chunk uploaded
	else
		$_SESSION['fileparts'][$sName]++; // 1 more part uploaded

	// Check if we have all parts
	if($_SESSION['fileparts'][$sName] == $_POST['totalchunks'])
	{
		// Re-assemble the file
		$fFinal = fopen($uploaddir . $sName, "w");

		// Loop and write other files
		for($i = 0; $i < $_POST['totalchunks']; $i++)
		{
			$fPart = fopen($partsdir . $sName . ".part" . $i, "r");
			while(!feof($fPart))
				fwrite($fFinal, fread($fPart, 8192));
			fclose($fPart);
			@unlink($partsdir . $sName . ".part" . $i);
		}

		// This file is done
		unset($_SESSION['fileparts'][$sName]);
	}

	// All replies MUST start with "POSTLET REPLY", if they don't, then Postlet will
	// not read the reply and will assume the file uploaded successfully.
	echo "POSTLET REPLY\r\n";
	// "YES" tells Postlet that this file was successfully uploaded.
    echo "POSTLET:YES\r\n";
	// End the Postlet reply
	echo "END POSTLET REPLY\r\n";
	exit;
} 
else
{
	// If the file can not be uploaded (most likely due to size), then output the
	// correct error code
	// If $_FILES is EMPTY, or $_FILES['userfile']['error']==1 then TOO LARGE
	if (count($_FILES)==0 || $_FILES['userfile']['error']==1){
		// All replies MUST start with "POSTLET REPLY", if they don't, then Postlet will
		// not read the reply and will assume the file uploaded successfully.
		header("HTTP/1.1 413 Request Entity Too Large");
		echo "POSTLET REPLY\r\n";
		echo "POSTLET:NO\r\n";
		echo "POSTLET:TOO LARGE\r\n";
		echo "POSTLET:ABORT THIS\r\n"; // Postlet should NOT send this file again.
		echo "END POSTLET REPLY\r\n";
		exit;
	}
	// Unable to write the file to the server ALL WILL FAIL
	else if ($_FILES['userfile']['error']==6 || $_FILES['userfile']['error']==7){
		// All replies MUST start with "POSTLET REPLY", if they don't, then Postlet will
		// not read the reply and will assume the file uploaded successfully.
		header("HTTP/1.1 500 Internal Server Error");
		echo "POSTLET REPLY\r\n";
		echo "POSTLET:NO\r\n";
		echo "POSTLET:SERVER ERROR\r\n";
		echo "POSTLET:ABORT ALL\r\n"; // Postlet should NOT send any more files
		echo "END POSTLET REPLY\r\n";
		exit;
	}
	// Unsure of the error here (leaves 2,3,4, which means try again)
	else {
		// All replies MUST start with "POSTLET REPLY", if they don't, then Postlet will
		// not read the reply and will assume the file uploaded successfully.
		header("HTTP/1.1 500 Internal Server Error");
		echo "POSTLET REPLY\r\n";
		echo "POSTLET:NO\r\n";
		echo "POSTLET:UNKNOWN ERROR\r\n";
		echo "POSTLET:RETRY\r\n";
		print_r($_REQUEST); // Possible usefull for debugging
		echo "END POSTLET REPLY\r\n";
		exit;
	}
}
?>
