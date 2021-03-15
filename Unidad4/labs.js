// JavaScript source code
document.getElementById('btNavbar').addEventListener('click', function (ev) {
    var navbar = document.getElementById('navbar');
    navbar.style.display = 'block';
    ev.stopPropagation();
});

window.addEventListener('click', function (ev) {
    var navbar = document.getElementById('navbar');
    if (navbar.style.display &&
        navbar.style.display !== 'none' &&
        ev.x > 200)
        navbar.style.display = '';
});
