<?php

include('../redirect.inc');
htmlHead("../");
?>
				<h1>Java and XHTML</h1>
				<h2>Why won't this validate?</h2>
			</div>
<?php
topLinks("../");
?>
			<div class="maincontent">
				<div class="padding">
					<p><a href="http://www.w3.org/TR/xhtml11/">XHTML 1.1</a> is a relatively modern standard, published first in 2001, and based upon <a href="http://www.w3.org/TR/xhtml1/">XTML 1.0</a>, which was first published in 2000.  Java on the other hand, has been around much longer, and has been supported by web browsers since 1995 when Netscape included it in their Navigator browser.</p><p>At this time, a much less restrictive version of markup language was being used, known simply as <a href="http://www.w3.org/TR/html4/">HTML</a>.  This allowed for all sorts of attributes and tags within the markup, which have now been deprecated by XHTML1.1.  However, to continue supporting older browsers, these tags need to be included.</p>
					<p>It is good technique to include both sets of tags within the document, to ensure that all browsers will be able to display the applet (assuming of course, that they have a suitable <a href="http://java.sun.com/j2se/1.4.2/download.html">JRE</a> installed.</p>
				</div>
			</div>
<?php
sideBar("../");
?>
			<div class="extend">
			</div>
		</div>
	</body>
</html>
