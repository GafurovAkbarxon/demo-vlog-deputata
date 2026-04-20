// svg  карта узбекистана
const tooltip = document.getElementById('tooltip');

document.querySelectorAll('.uz-map path').forEach(region => {
    region.addEventListener('click', () => {
        const regionId = region.id;
        window.location.href = `/ru/uzbekistan/${regionId}`; // или своя логика
    });

    region.addEventListener('mouseenter', e => {
        tooltip.innerText = region.dataset.name;
        tooltip.style.display = 'block';
    });

    region.addEventListener('mousemove', e => {
        tooltip.style.left = e.pageX + 10 + 'px';
        tooltip.style.top = e.pageY + 10 + 'px';
    });

    region.addEventListener('mouseleave', () => {
        tooltip.style.display = 'none';
    });
});


// =========================================