(function () {
    var Upload = function Upload() {
        this.init();
    };

    Upload.prototype.init = function () {
        var fileInput = document.createElement("input");
        fileInput.style = 'position: absolute; left: -999px;';
        fileInput.setAttribute('type', 'file');

        dom.append(fileInput);

    };


    window['Upload'] = new Upload();
})();