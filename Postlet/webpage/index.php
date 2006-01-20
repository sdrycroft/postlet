<?php

include('redirect.inc');
htmlHead("");
?>
				<h1>POSTLET</h1>
				<h2>Java applet for the transfer of files to an HTTP server</h2>
			</div>
<?php
topLinks("");
?>
			<div class="maincontent">
				<div class="padding">
					<p>Postlet is a Java applet used to enable websites to allow their users to send multiple files to a webserver with a few simple clicks.</p>
					<p>Advantages of Postlet over normal HTTP file upload from a web browser include:</p>
					<ul><li>File progress bar - Of particular use for large uploads, or users with slow connections.</li><li>Error checking - The applet will automatically retry a file upload should it fail for any reason.</li><li>Easy selection of multiple files</li></ul>
					<p>For updates to the Postlet project, either check back regularly, or check the <a href="http://sourceforge.net/projects/postlet/">Postlet Sourceforge project page.</a></p>
					<p>The 0.5 release of Postlet is available for download.  For an example of how the applet works, please see my <a href="example/">example page</a></p>
					<p>The example page shows the current CVS build.  This implements a number of features over and above the current stable release.  These include:</p>
					<ul><li>Multiple clicking of add button enabled</li><li>Alternate languages (default English).</li><li>User defined language files</li><li>Selection of directories enabled</li></ul>
				</div>
			</div>
<?php
sideBar("");
?>
			<div class="extend">
			</div>
		</div>
	</body>
</html>
