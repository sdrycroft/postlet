<?php
include('../redirect.inc.php');
htmlHead();
topLinks();
?>
					<p>This page is merely intended to show users Postlet in it's working state.  If it does not work for you, please <a href="mailto:sdrycroft@users.sourceforge.net">contact</a> the author.</p>
					<p>Files will be uploaded to a directory on the Sourceforge server, and when complete you'll be taken to a page where the files will be listed to show that it worked.  If you use image files, then thumbnails of the images will be produced, and outputed to your web browser.  All files will be immediately deleted once they have been used, so you will not be able to refresh the success page.</p>
					<p>Try uploading an image file, along with a PHP file (any text file with the extension changed to .php).  You'll find the image file will upload, whilst the PHP file will not.  This shows how you can give feedback to your users.</p>
					<div id="finishedmessage" class="finishedmessage"></div>
					<p>You can now use Postlet via JavaScript.  Just look at the source of this page to see how. <a href="#" onClick="document.postlet.postletAdd();">Add Click</a> <a href="#" onClick="document.postlet.postletUpload();">Upload Click</a></p>
						<applet name="postlet" code="Main.class" archive="postlet.manifest.jar" width="305" height="200" mayscript="mayscript">
							<param name = "maxthreads"		value = "5" />
							<param name = "language"		value = "" />
							<param name = "type"			value = "application/x-java-applet;version=1.3.1" />
							<param name = "destination"		value = "http://postlet.com/example/javaUpload.php" />
							<param name = "backgroundcolour" value = "16777215" />
							<param name = "tableheaderbackgroundcolour" value = "14079989" />
							<param name = "tableheadercolour" value = "0" />
							<param name = "warnmessage" value = "false" />
							<param name = "autoupload" value = "true" />
							<?php
if(isset($_SERVER['HTTP_VIA']))
{
	$proxyHeaders = split(",",$_SERVER['HTTP_VIA']);
	$proxyHeaders = split(" ",$proxyHeaders[0]);
	$proxyHeaders = split(":",$proxyHeaders[1]);
	echo '<param name = "proxy" value="'.$_SERVER['REMOTE_ADDR'].':'.$proxyHeaders[1].'">';
} else	{
	echo '<param name = "proxy" value="false">';
}?>
							<param name = "helpbutton" value = "false" />
							<param name = "removebutton" value = "false" />
							<param name = "addbutton" value = "false" />
							<param name = "uploadbutton" value = "false" />
							<param name = "fileextensions" value = "Image Files,jpg,gif,jpeg" />
							<param name = "endpage" value = "[This is unset here]" />
							<param name = "helppage" value = "http://www.postlet.com/help/?thisIsTheDefaultAnyway" />
						</applet>
					</p>
					<p>Once upload is complete you'll be taken to the <a href="../success/">success page</a>, if you're not taken there straight away, then just click this link.</p>
					<p>Don't see anything special above, the page should look like <a href="../images/example.jpg">this</a>.  There are a number of possible reasons for this, the most obvious one bein that you don't have a suitable Java Virtual Machine (JVM) installed.  You can <a href="http://www.javatester.org/version.html">check which JVM</a> (<a href="../version/">postlet.com mirror</a>) you have installed, and change it if necessary (NB, this applet will NOT work with any version of the Microsoft JVM).  Suitable JVMs can be obtained from <a href="http://www.java.com">Sun</a>, or a number of other sites.</p>
<?php
htmlFooter();
