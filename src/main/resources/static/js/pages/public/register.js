const username = document.getElementById("username");
const password = document.getElementById("password");
const passwordConfirm = document.getElementById("passwordConfirm");
const btnSubmit = document.getElementById("submitBtn");
const usernameErr = document.getElementById("usernameError");
const passwordErr = document.getElementById("passwordError");
const passwordConfirmErr = document.getElementById("confirmError");
const form = document.getElementById("registerForm");


username.addEventListener("input",updateButton);
password.addEventListener("input",updateButton);
passwordConfirm.addEventListener("input",updateButton);





// валидация
function validateUsername() {

    const login = username.value.trim();
    const regex = /^[A-Za-z0-9_]+$/;

    if (login.length < 3 || login.length > 30) {
        setError(username, usernameErr, messages.usernameSize);
        return false;
    }
    if (!regex.test(login)) {
        setError(username, usernameErr, messages.usernamePattern);
        return false;
    }

    setValid(username);
    return true;
}


function validatePassword(){
    const parol=password.value.trim()
    if (parol.length < 6 || parol.length >30) {
        setError(password, passwordErr, messages.passwordSize);
        return false;
    }
    setValid(password);
    return true;
}

function validateConfirm(){
    const parolConfirm=passwordConfirm.value.trim()
    const parol=password.value.trim()
    if (parolConfirm !== parol) {
        setError(passwordConfirm, passwordConfirmErr, messages.passwordMismatch);
        return false;
    }
    setValid(passwordConfirm);
    return true;
}
// ===
function setError(input, el, message){
    input.classList.add("is-invalid");
    input.classList.remove("is-valid");
    el.textContent = message;
}

function setValid(input){
    input.classList.remove("is-invalid");
    input.classList.add("is-valid");
}

function updateButton(){
    const usernameValid = validateUsername();
    const passwordValid = validatePassword();
    const confirmValid = validateConfirm();

    btnSubmit.disabled = !(usernameValid && passwordValid && confirmValid);
}
// ===
// снять шифр маску пароля
function togglePassword(inputId, button) {
    const input = document.getElementById(inputId);
    const icon = button.querySelector("i");

    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove("bi-eye");
        icon.classList.add("bi-eye-slash");
    } else {
        input.type = "password";
        icon.classList.remove("bi-eye-slash");
        icon.classList.add("bi-eye");
    }
}
// ===
// капча лоигка сервре приказал принести капчу


window.onTurnstileSuccess = function(token) {
    document.getElementById("captchaToken").value = token;
};
// ===

