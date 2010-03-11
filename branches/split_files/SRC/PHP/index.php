<?php

// Start a session to propagate through the applet uploads
session_name('postlet');
session_start();

function returnBytes($sShorthand)
{
	$iSize = intval($sShorthand);
	switch(strtolower($sShorthand[strlen($sShorthand) - 1]))
	{
		case 'g':
			$iSize *= 1024;
		case 'm':
			$iSize *= 1024;
		case 'k':
			$iSize *= 1024;
	}

	return $iSize;
}

?>
<html>

	<head>
		<title>Applet test</title>
		<script type="text/javascript">
		function postletFinished()
		{
			alert('done');
		}
		</script>
	</head>
	
	<body>
		<applet name="postlet" code="Main.class" archive="postlet.jar" width="305" height="150" mayscript>
			<param name = "maxthreads"		value = "5" />
			<param name = "language"		value = "" />
			<param name = "type"			value = "application/x-java-applet;version=1.3.1" />
			<param name = "destination"		value = "http://localhost/postlet/javaUpload.php" />
			<param name = "backgroundcolour" value = "16777215" />
			<param name = "tableheaderbackgroundcolour" value = "14079989" />
			<param name = "tableheadercolour" value = "0" />
			<param name = "warnmessage" value = "false" />
			<param name = "autoupload" value = "false" />
			<param name = "helpbutton" value = "false" />
			<param name = "fileextensions" value = "Image Files,jpg,gif,jpeg" />
			<param name = "endpage" value = "" />
			<param name = "helppage" value = "http://www.postlet.com/help/?thisIsTheDefaultAnyway" />
			<param name = "maxfilesize" value="<?= returnBytes(ini_get("upload_max_filesize")); ?>" />
			<param name = "splitfiles" value="true" />
		</applet>
		<!--
			If the ENDPAGE URL is left unset, you must have a postletFinished 
			javascript method on your HTML page.
			
			DESTINATION URL is the url on your server of the script that will
			receive the files.
			
			More information about the above can be found on the Postlet install
			page: http://www.postlet.com/install
		-->
		<p>Check <a href="http://www.postlet.com/install/">here</a> first if this
		isn't working, and then post a message <a href="http://sourceforge.net/forum/forum.php?forum_id=468216">here</a>.</p>
	</body>
</html>
