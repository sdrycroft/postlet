<?php

$currentRelease = "0.15";
$releaseDate    = "4 SEPTEMBER 2008";

ini_set("session.use_cookies",true);
ini_set("session.use_only_cookies",true);
ini_set('session.use_trans_sid', false);
ini_set("session.cookie_domain","www.postlet.com");
ini_set("arg_separator.output","&amp;");
session_start();

function topLinks() {
echo '<div class="topLinks">
				<p><a href="/">Home</a><a href="http://sourceforge.net/projects/postlet/">SF Site</a><a href="/install/">Install</a><a href="/help/">Usage Help</a><a href="/example/">Example</a><a href="http://sourceforge.net/project/showfiles.php?group_id=136812">Download</a><a href="/scripts/">Server scripts</a></p>
			</div>';
}
function htmlFooter(){
?>
				<div class="bottom">
					<p><a href="http://simon.rycroft.name/">Simon Rycroft</a> | <a href="https://sourceforge.net/donate/index.php?group_id=136812">Donate</a> | <a href="http://sourceforge.net"><img src="http://sflogo.sourceforge.net/sflogo.php?group_id=136812&amp;type=1" width="44" height="16" border="0" alt="SourceForge.net Logo" /></a></p>
				</div>
			</div>
		</div>
		<div class="footer"></div>
  <div class="adverts"><div><script type="text/javascript"><!--
	google_ad_client = "pub-8730702441799610";
	/* Postlet Horizontal (wide) */
	google_ad_slot = "2219856888";
	google_ad_width = 728;
	google_ad_height = 90;
	//-->
	</script>
	<script type="text/javascript"
	src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
	</script>
  <div></div>
	</body>
</html>
<?php
}
function htmlHead(){
	global $releaseDate;
	global $currentRelease;
	if (isset($_COOKIE['PHPSESSID'])){
		session_id($_COOKIE['PHPSESSID']);
	}
	if(session_status() == PHP_SESSION_NONE){
		session_start();
	}
	echo '<?xml version="1.0" encoding="UTF-8"?>'; // Fucking short tags!
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" >
	<head>
		<title>Postlet :: Java Upload Applet</title>
		<style type="text/css" media="screen">@import "/style.css";</style>
		<link type="image/x-icon" rel="shortcut icon" href="/favicon.ico"/>
		<link rel="openid.server" href="http://openid.simon.rycroft.name/"/>
		<link rel="openid.delegate" href="http://openid.simon.rycroft.name/"/>
		<script type="text/javascript" src="postlet.js"></script>
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
<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<!-- Postlet Horizontal (wide) -->
<ins class="adsbygoogle"
     style="display:inline-block;width:728px;height:90px"
     data-ad-client="ca-pub-8730702441799610"
     data-ad-slot="2219856888"></ins>
<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
</script>
  <div class="header">
  	<div class="header-container">
  		<div class="header-news">
  		  <h1><a href="http://sourceforge.net/project/showfiles.php?group_id=136812"><?php echo $currentRelease ." - ".$releaseDate;?></a></h1>
  		  <p>Postlet is now able to resize images which are larger than a (user) set size, before uploading them to a server.  Recently added is the ability to replace the files table with a "drop image" - See the <a href="http://www.postlet.com/svn/?language=empty">SVN page</a> for an example.  Languages include <a href="/example/">EN</a>, <a href="/example/?language=DE">DE</a>, <a href="/example/?language=NL">NL</a>, <a href="/example/?language=FR">FR</a>, <a href="/example/?language=ES">ES</a>, <a href="/example/?language=IT">IT</a>, <a href="/example/?language=NO">NO</a>, <a href="/example/?language=TU">TU</a>, <a href="/example/?language=FI">FI</a>. <br/><a href="http://prdownloads.sourceforge.net/postlet/postlet-<?php echo $currentRelease; ?>.zip?download">DOWNLOAD the binary.</a></p> 
					</div>
  	</div>
  </div>
  <div class="container">
  	<div class="content">
<?php
}
