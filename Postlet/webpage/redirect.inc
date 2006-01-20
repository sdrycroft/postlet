<?php
$locationOfFirstSlash = strpos($_SERVER['SCRIPT_URI'],"/",7);
if (substr($_SERVER['SCRIPT_URI'],7,$locationOfFirstSlash-7)!="www.postlet.com"){
	header ('HTTP/1.1 301 Moved Permanently');
	header ("Location: http://www.postlet.com/".substr($_SERVER['SCRIPT_URI'],$locationOfFirstSlash+1));
	echo '<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN"><html><head><title>301 Moved Permanently</title></head><body><h1>Moved Permanently</h1><p>The document has moved <a href="http://www.postlet.com/">here</a>.</p></body></html>';
	exit;
}
function sideBar($string, $w3yes=true) {
echo '
			<div class="sidebar">
				<div class="padding">
					<h1>Current Release</h1>
					<p><a href="http://sourceforge.net/project/showfiles.php?group_id=136812&amp;package_id=152148&amp;release_id=371354">0.5 - 16.11.2005</a></p><p>Fixes a major bug with the way Content-Length headers were set.<br/><a href="http://sourceforge.net/project/showfiles.php?group_id=136812&amp;package_id=152148&amp;release_id=371354">Download the current release</a></p> 
					<h1>Links</h1>
					<div class="links">
					<p><a href="http://java.sun.com">Java (Sun)</a></p>
					</div>
					<h1>Developer</h1>
					<p><a href="http://www.simon.rycroft.name">S.D.Rycroft</a></p>
					<h1>Donate</h1>
					<p><a href="http://sourceforge.net/donate/index.php?group_id=136812">Help me live</a></p>
					<h1>Hosting</h1>
					<p>
						<a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id=136812&amp;type=1" width="88" height="31" alt="SourceForge.net Logo" /></a><br/>
						';
if ($w3yes){
	echo '<a class="firstW3" href="http://validator.w3.org/check?uri=referer"><img src="'.$string.'images/xhtml.jpg" alt="Valid XHTML 1.1" /></a><br/>
						<a class="W3" href="http://jigsaw.w3.org/css-validator/check/referer"><img src="'.$string.'images/css.jpg" alt="Valid CSS!" /></a>';
}
else{
	echo '<a class="firstW3" href="'.$string.'notcompliant/"><img src="'.$string.'images/xhtml.jpg" alt="Valid XHTML 1.1" /></a><br/>
						<a class="W3" href="'.$string.'notcompliant/"><img src="'.$string.'images/css.jpg" alt="Valid CSS!" /></a>';
}
echo '
					</p>
				</div>
			</div>';
}

function topLinks($string) {
echo '
			<div class="topLinks">
				<p><a href="'.$string.'">Home</a> | <a href="'.$string.'install/">Install</a> | <a href="'.$string.'help/">Usage Help</a> | <a href="'.$string.'example/">Example</a> | <a href="http://sourceforge.net/project/showfiles.php?group_id=136812">Download</a> | <a href="'.$string.'scripts">Server scripts</a></p>
			</div>
';
}
function htmlHead($string){
echo '<?xml version="1.0" encoding="UTF-8"?>
';
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" >
	<head>
		<title>Postlet :: Java Upload</title>
		<link rel="stylesheet" title="Default" href="<?php echo $string."style.css"; ?>" type="text/css" media="screen"/>
		<link type="image/x-icon" rel="shortcut icon" href="http://www.postlet.com/favicon.ico"/>
		<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
		<meta http-equiv="content-language" content="en"/>
		<meta 	name="Author" 
			content="Simon Rycroft"/>
		<meta 	name="owner"
			content="Simon Rycroft"/>
		<meta	name="copyright"
			content="&copy; 2005 Simon Rycroft"/>

		<meta	name="description" 
			content="Postlet: A java applet for the uploading of files to an HTTP server"/>
		<meta	name="robots"
			content="all"/>
		<meta	name="robots"
			content="index,follow"/>
		
		<meta 	name="keywords" 
			xml:lang="EN" 
			content="java, applet, java applet, multiple, multiple file, file, upload, multiple file upload,
				     http, server, post, postlet, postlet.com, transfer, java upload applet, java upload,
				     apache, iis, many, lots, more than one, easy"/>
	</head>	
	<body>
		<div class="container">
			<div class="title">
<?php
}
?>