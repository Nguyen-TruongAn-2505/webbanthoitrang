//cancel button
$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location = moduleURL ;
    });

    //"[[@{/users}]]"

    //xu ly font hinh anh
    $("#fileImage").change(function () {
        //xac thuc kich thuoc tep hinh anh ban dau
        fileSize = this.files[0].size;
        //1 mb = 1048576 b
        if (fileSize > 204800) {
            //ngan bieu mau toi may chu va hien thi thong bao loi
            this.setCustomValidity("you must choose an images less than 200 KB");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this)
        }
    });
});

<!--xu ly font hinh anh xong goi vao document-->
function showImageThumbnail(fileInput) {
    //1 anh ban dau
    var file = fileInput.files[0];
    //khai bao 1 trinh doc anh
    var reader = new FileReader();
    //chi dinh su kien onload cho reader
    reader.onload = function (e) {
        //chon hinh thu nho va dat thuoc tinh atribule la src
        $("#thumbnail").attr("src", e.target.result);
    };
    //chuyen doi doi tuong tep
    reader.readAsDataURL(file);
}