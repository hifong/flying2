
var editor;
KindEditor.ready(function (K) {
    editor = K.create('textarea[name="content"]', {
        allowFileManager: false,
        resizeType: 1,
        allowPreviewEmoticons: false,
        items: [
            'preview', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
            'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
            'insertunorderedlist', 'quickformat', '|', 'emoticons', 'image', 'link','pagebreak','|', 'fullscreen','source'],
        afterChange: function () {
            this.sync();
        }
    });
});
$(document).ready(function(){
	//$('#category_id').combobox("setValue", '19');
	//$('#category_id').combobox("select", '19');
});