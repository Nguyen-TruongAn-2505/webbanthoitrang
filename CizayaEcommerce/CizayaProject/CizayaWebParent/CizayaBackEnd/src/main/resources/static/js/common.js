

//ready() Đây là bộ quản lý sự kiện cơ bản của Jquery.
// bo quan ly ready() ve co ban giong $(window).
//su kien log out
$(document).ready(function () {
    $("#logoutLink").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });

    customizeDropDownMenu();
});


//funtion tuy chinh thanh drop down menu
//khi an vao ten truoc khi log in se hien ra mot request moi den trang account
// dung navbar .dropdown de dieu huong . khi hover vaof custimuzaDropMenufuncion
//250 la do tre khi an vao button
// 100 la do tre khi an vao log out
function customizeDropDownMenu(){
    $(".navbar .dropdown").hover(
        function (){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        function (){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        },
    );
    $(".dropdown >a").click(function (){
        location.href = this.href;
    })
}