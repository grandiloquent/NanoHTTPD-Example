(function () {

    var Modal = function Modal() {
    };

    Modal.prototype.hide = function () {
        if (this.dialog) {
            document.body.removeChild(this.dialog);
            this.dialog = null;
        }
    };


    Modal.prototype.show = function (title, description, button) {
        if (title) {
            title = "<div class=\"info-tit\"><div class=\"info-tit-con\"><span class=\"tit\">" + title + "</span> </div></div>";
        } else {
            title = '';
        }
        if (description) {
            description = "<div class=\"info-desc\"><p class=\"txt\">" + description + "</div>";
        } else {
            description = '';
        }
        if (button) {
            button = "<button class=\"btn btn-active\">" + button + "</button>"
        } else {
            button = '';
        }
        var root = document.createElement('div');

        root.setAttribute('id', 'dialog');

        root.innerHTML = "<div class=\"modal modal-show\"><b class=\"modal-mask\"></b><div class=\"modal-dialog modal-dialog-pop\"><div class=\"modal-dialog-hd clearfix\"><button aria-label=\"关闭弹窗\" class=\"btn-icon icon icon-pop-close\"></button></div><div class=\"modal-dialog-bd\"><div class=\"modal-bd-cont clearfix\"><div class=\"modal-info\">" + title + "" + description + "</div></div></div><div class=\"modal-dialog-ft clearfix btn-group\">" + button + "</div></div></div>";

        document.body.append(root);

        var close = root.querySelector('.icon-pop-close');
        var that = this;
        close && close.addEventListener('click', function (evt) {
            that.hide();
        });
        this.dialog = root;
    };

    window['Modal'] = new Modal();
})();