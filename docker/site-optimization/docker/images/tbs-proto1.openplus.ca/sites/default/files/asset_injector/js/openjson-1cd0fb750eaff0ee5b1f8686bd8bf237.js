//Opens JSON file & returns as JS Object (With error handling)
function openJSON(filelocation){
    if( filelocation.substring(0, 6) != "http://" || filelocation.substring(0, 7) != "https://" ){
        filelocation = "http://localhost:8000"+filelocation;
    }
    try{ 
        var request = new XMLHttpRequest();
        request.open("GET", filelocation, false); request.send(null);
        var response = JSON.parse(request.responseText);
        console.log("Successfully converted JSON file '"+filelocation.toString()+"' to JS Object."); console.log(response); return response;
    }
    catch(error){
        console.log("Error fetching JSON... It is likely not hosted locally."); console.log(error); return null;
    }
}

