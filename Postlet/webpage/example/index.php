<?php

include('../redirect.inc');
htmlHead("../");
?>
				<h1>Test page</h1>
				<h2>try out postlet for yourself</h2>
			</div>
<?php
topLinks("../");
?>
			<div class="maincontent">
				<div class="padding">
					<p>This page is merely intended to show users Postlet in it's working state.  If it does not work for you, please <a href="mailto:sdrycroft@users.sourceforge.net">contact</a> the author.</p>
					<p>Files will be uploaded to a directory on the Sourceforge server, and when complete you'll be taken to a page where the files will be listed to show that it worked (this is currently broken, and I have no idea how to fix it.  The problem appears to be with permisions on the Sourceforge server, if anyone knows how to fix this, please help).</p>
					<p>
						<applet width="305" height="150">
							<param name = "language"		value = "<?php echo $_GET['language'] ?>" />
							<param name = "language"		value = "EN" />
							<param name = "CODE" 			value = "Main.class" />
							<param name = "ARCHIVE"			value = "Postlet.jar" />
							<param name = "NAME"			value = "Postlet" />
							<param name = "type"			value = "application/x-java-applet;version=1.3.1" />
							<param name = "scriptable" 		value = "false" />
							<param name = "destination"		value = "http://postlet.sourceforge.net/example/javaUpload.php" />
							<param name = "endpage" 		VALUE = "http://postlet.sourceforge.net/success/" />
							<param name = "red" 			VALUE = "255" />
							<param name = "green" 			VALUE = "255" />
							<param name = "blue" 			VALUE = "255" />
							<param name = "redheaderback" 	VALUE = "240" />
							<param name = "greenheaderback" VALUE = "240" />
							<param name = "blueheaderback" 	VALUE = "240" />
							<param name = "redheader" 		VALUE = "200" />
							<param name = "greenheader" 	VALUE = "200" />
							<param name = "blueheader" 		VALUE = "200" />
						</applet>
					</p>
					<p>Don't see anything special above, the page should look like <a href="../images/example.jpg">this</a>.  There are a number of possible reasons for this, the most obvious one bein that you don't have a suitable Java Virtual Machine (JVM) installed.  You can <a href="http://www.javatester.org/version.html">check which JVM</a> you have installed, and change it if necessary (NB, this applet will NOT work with any version of the Microsoft JVM).  Suitable JVMs can be obtained from <a href="http://www.java.com">Sun</a>, or a number of other sites.</p>
				</div>
			</div>
<?php
sideBar("../", false);
?>
			<div class="extend">
			</div>
	</body>
</html>