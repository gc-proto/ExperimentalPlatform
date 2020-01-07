//Abstract function to fill '<datalist>' with '<option>' data
//Note: Delimiters should be an array of search tags ["delimiter1", "delimiter2"]
//Values & Labels must be in dataset
function fillDataList(id, dataset, delimiters,value,label){
    var datalist = document.getElementById(id);
    console.log(dataset);
    dataset = JSON.parse(dataset); 
    for (var i = 0; i < dataset.length; i++) {
        var delims = "";
        for(var j=0; j < delimiters.length; j++){
            var thisDelimiter = delimiters[j]; var thisDelimiterData = JSON.stringify(dataset[i][thisDelimiter]);
            delims = delims + 'data-'+JSON.stringify(thisDelimiter).slice(1,-1)+'='+thisDelimiterData+' ';
        }
        datalist.innerHTML = datalist.innerHTML + '<option value="'+dataset[i][value]+'" label="'+(dataset[i][label] || '' )+'" '+delims+'></option>';
    }
}