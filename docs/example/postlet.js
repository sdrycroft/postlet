function postletFinished(){
  alert("Finished");
  var finishedMessageString = "<p>Upload results:</p>";
  var failedfilesstring = document.postlet.javascriptGetFailedFiles();
  if (failedfilesstring != null){
    var failedfiles = failedfilesstring.split("/");
    if (failedfiles.length >0)
      finishedMessageString += "<ul>Failed files:";
    for (var i=0; i<failedfiles.length; i++)
      finishedMessageString += "<li>"+failedfiles[i]+"</li>";
    if (failedfiles.length >0)
      finishedMessageString += "</ul>";
  }
  var successfilesstring = document.postlet.javascriptGetUploadedFiles();
  if (successfilesstring != null){
    var successfiles = successfilesstring.split("/");
    if (successfiles.length>0)
      finishedMessageString += "<ul>Successful files:";
    for (var i=0; i<successfiles.length; i++)
      finishedMessageString += "<li>"+successfiles[i]+"</li>";
    if (successfiles.length>0)
      finishedMessageString += "</ul>";
  }
  finishedMessageString +="<p>See the successful files <a href=\"../success/\">here</a></p>";
  document.getElementById("finishedmessage").innerHTML=finishedMessageString;
  document.getElementById("finishedmessage").style.display="block";
  //window.location="http://www.postlet.com/success/";
  return true;
}

function postletStatus(percent){
  /*document.getElementById("percentcomplete").innerHTML=percent+"%";*/
}
function postletFiles(filesString){ 
}
function postletError(errorCode, errorString){
}
