<?php
#################################################
#
# S.D.Rycroft 1/12/2005
#
# Change the following to where you want files to be saved on your server.
# N.B. This needs to be the full path to the directory to save to.
#
$uploaddir = '/path/to/directory';
#
#################################################

// Original filename of the file uploaded is used.  If the file is already present, it will
// be overwritten.
$fname = $_FILES['userfile']['name'];

if (move_uploaded_file($_FILES['userfile']['tmp_name'], $uploaddir .$fname))
{
    echo "***YES***";
} 
else
{
    echo "***NO***";
}
?>