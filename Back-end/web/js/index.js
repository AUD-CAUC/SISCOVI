$('.message a').click(function(){
    $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});

function myFunction(){
    location.href="test/welcome.html";
}