<?php
include('../redirect.inc.php');
htmlHead();
topLinks();
?>
<p>This page is here to show you what version of Java you're using.</p>
<applet height="60" alt="Browser has Java disabled" width="305" code="JavaVersionDisplayApplet.class"></applet>
<p>Full information about this applet can be obtained from the <a href="http://www.javatester.org/sampleresults.html">Javatester.org website</a>
<?php
htmlFooter();