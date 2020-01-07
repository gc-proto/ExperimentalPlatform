var sel = document.getElementById('search_options');
sel.onchange = function () {
    document.getElementById("type").href = this.value + ".html";
}