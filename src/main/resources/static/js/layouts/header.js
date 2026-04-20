const CURRENT_LANG =
    document.documentElement.lang ||      // из <html lang="">
    document.body.dataset.lang ||         // или data-lang
    "ru";

document.addEventListener('DOMContentLoaded', () => {

    const filterToggle = document.getElementById('filterToggle');
    const filterPanel  = document.getElementById('filterPanel');
    const applyBtn     = document.getElementById('applyFilters');
    const clearBtn     = document.getElementById('clearFilters');
    const searchField  = document.getElementById('searchField');
    const searchTag  = document.getElementById('searchTag');
    const mobileSearchTag  = document.getElementById('mobileSearchTag');
    const mobileSearchTitle  = document.getElementById('mobileSearchTitle');

    /* ===== ОТКРЫТЬ / ЗАКРЫТЬ ФИЛЬТРЫ ===== */
    if (filterToggle && filterPanel) {
        filterToggle.addEventListener('click', () => {
            const visible = filterPanel.style.display === 'block';
            filterPanel.style.display = visible ? 'none' : 'block';
            filterToggle.setAttribute('aria-expanded', (!visible).toString());
        });
    }

    /* ===== ОБЩАЯ ФУНКЦИЯ ПЕРЕХОДА ===== */
    function goWithFilters(title, category, tag, sort) {
        const params = new URLSearchParams();

        if (title)    params.set('filterTitle', title);
        if (category) params.set('filterCategory', category);
        if (tag)      params.set('filterTag', tag);
        if (sort)     params.set('sort', sort);

        window.location.href = `/${CURRENT_LANG}/article/all?` + params.toString();
    }

    /* ===== ПРИМЕНИТЬ ФИЛЬТРЫ (DESKTOP) ===== */
    if (applyBtn) {
        applyBtn.addEventListener('click', () => {
            goWithFilters(
                searchField?.value.trim(),
                document.getElementById('filterCategory').value,
                searchTag?.value.trim(),
                document.getElementById('filterSort').value
            );
        });
    }

    /* ===== ENTER В ПОИСКЕ (DESKTOP) ===== */
    if (searchField) {
        searchField.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                goWithFilters(
                    searchField.value.trim(),
                    document.getElementById('filterCategory').value,
                    searchTag?.value.trim(),
                    document.getElementById('filterSort').value,
                );
            }
        });
    }

    /* ===== СБРОС ===== */
    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            window.location.href = `/${CURRENT_LANG}/article/all`;
        });
    }

    /* ===== MOBILE SEARCH ===== */
    const mobileBtn = document.getElementById('mobileSearchBtn');
    if (mobileBtn) {
        mobileBtn.addEventListener('click', () => {

            const title=mobileSearchTitle?.value.trim()
            const category=   document.getElementById('mobileFilterCategory').value
            const tag=    mobileSearchTag?.value.trim()
            const sort=  document.getElementById('mobileFilterSort').value

            const modalEl = document.getElementById('searchModal');
            const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
            modal.hide();

            goWithFilters(title, category, tag, sort);
        });
    }
});


function changeLang(newLang) {
    const path = window.location.pathname;   // /ru/article/slug
    const parts = path.split('/').filter(Boolean);

    if (parts.length === 0) return;

    parts[0] = newLang; // меняем язык

    const newPath = '/' + parts.join('/');
    const query = window.location.search;

    window.location.href = newPath + query;
}
