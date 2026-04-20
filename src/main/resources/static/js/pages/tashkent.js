// кнопки нажатия годов

const buttons = document.querySelectorAll('.year-btn');
const text = document.getElementById('yearText');

buttons.forEach(btn => {
    btn.addEventListener('click', () => {
        buttons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        text.textContent = btn.dataset.text;
    });
});
// ===============