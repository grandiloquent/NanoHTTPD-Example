(function () {

  var App = function App() {

  };

  App.prototype.init =
    function () {
      this.bindViews();
      this.initViews();
    }

  App.prototype.bindViews = function () {
    this.upload = document.getElementById('formFileInputCt');
  };
  App.prototype.initViews = function () {
    if (this.upload) {
      var that = this;
      this.upload.addEventListener('click', function () {
        that.toggleUploadMenu();
      });
    }
  };
  App.prototype.toggleUploadMenu =
    function () {
      if (this.upload.classList.contains('act')) {
        this.upload.classList.remove('act');
      } else {
        this.upload.classList.add('act');
      }
    }

  var app = new App();
  app.init();
})();