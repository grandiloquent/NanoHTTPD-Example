(function () {
    var Upload = function Upload() {
        this.init();
    };

    Upload.prototype.init = function () {

    };

    Upload.prototype.uploadFile = function (callback) {
        var fileInput = document.createElement("input");
        fileInput.style = 'position: absolute; left: -999px;';
        fileInput.setAttribute('type', 'file');

        dom.append(fileInput);
        this.fileInput = fileInput;
        this.fileInput.click();
        this.fileInput.addEventListener('change', function () {
            uploadFile(this.files, callback);
        })
    }

    function uploadFile(fileList, callback) {

        if (fileList.length == 0) return;
        var formData = new FormData();
        formData.append('file', fileList[0], fileList[0].name);
        fetch("/upload", {
            method: 'POST',
            body: formData
        }).then(function (response) {
            if (!response.ok) {
                throw Error(response.statusText);
            } else {
                return response.json();
            }
        }).then(function (json) {
            if (callback)
                callback(null, json);
        }).catch(function (err) {
            if (callback)
                callback(err);
        });

    }
    window['Upload'] = new Upload();
})();