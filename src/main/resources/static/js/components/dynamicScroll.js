const observer = new IntersectionObserver(
    entries => {
        entries.forEach(entry => {
            entry.target.classList.toggle('active', entry.isIntersecting);
        });
    },
    {
        threshold: 0.15
    }
);

document.querySelectorAll('.reveal').forEach(el => {
    observer.observe(el);
});