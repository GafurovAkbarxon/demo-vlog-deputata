function updateSubmitState() {
    $('#submitBtn').prop('disabled', $('.block').length === 0);
}

$(document).on('click', '[onclick^="addBlock"]', updateSubmitState);
$(document).on('click', '[onclick^="removeBlock"]', updateSubmitState);

let firstError = true;
function addBlock(type) {
    const templateMap = {
        text: 'textBlockTemplate',
        image: 'imageBlockTemplate',
        quote: 'quoteBlockTemplate'
    };

    const block = document.getElementById(templateMap[type]).content.cloneNode(true);

    if (type === 'text' || type === 'quote') {
        const toolbar = document.getElementById('toolbarButtonsTemplate').content.cloneNode(true);
        block.querySelector('.card-body').prepend(toolbar);
    }
    $('#blocksContainer').append(block);
}



// Удаление блока
function removeBlock(btn) {
    $(btn).closest('.block').remove();
}
function saveBlocksToHiddenInput() {
    const blocks = collectBlocks();
    $('#blocksJson').val(JSON.stringify(blocks));
}
function collectBlocks() {
    const blocks = [];

    $('.block').each(function () {
        const $block = $(this);

        const type = $block.find('input[data-key="type"]').val();
        const content = $block.find('input[data-key="content"]').val() || null;

        if (!type) return;

        blocks.push({
            type: type,
            content: content
        });
    });

    return blocks;
}


function saveEditableContent($block) {
    if (!$block.find('[contenteditable]').length) return;
    const html = $block.find('[contenteditable]').html()?.trim() || '';

    let input = $block.find('input[data-key="content"]');
    if (!input.length) {
        input = $('<input>', {
            type: 'hidden',
            'data-key': 'content'
        }).appendTo($block);
    }

    input.val(html);
}
function validateBlocks() {
    let isValid = true;
    firstError = true;

    const blocks = $('.block');
    // ❗ НЕТ НИ ОДНОГО БЛОКА
    if (blocks.length === 0) {
        alert('Добавьте хотя бы один блок в статью');
        return false;
    }

    $('.block').each(function (index) {
        const $block = $(this);
        const type = $block.data('type');

        $block.removeClass('border border-danger');

        if (type === 'text' || type === 'quote') {
            const content = $block.find('[contenteditable]').html()?.trim();

            if (isEmptyHtml(content)) {
                blockInvalid($block, type, index);
                isValid = false;
            }
        }

        if (type === 'image') {
            const file = $block.find('input[type="file"]')[0];
            const existing = $block.find('input[data-key="content"]').val();


            if ((!file || file.files.length === 0) && !existing) {
                blockInvalid($block, type, index);
                isValid = false;
            }
        }
    });

    return isValid;
}
function blockInvalid($block, type, index) {
    $block.addClass('border border-danger');

    const labels = {
        text: 'Пустой текстовый блок',
        quote: 'Пустая цитата',
        image: 'Картинка не выбрана'
    };
    if (!firstError) return;
    firstError = false;
    alert(`Блок #${index + 1}: ${labels[type]}. Заполните или удалите.`);
}
$(document).on('change', '.image-input', function () {
    const file = this.files[0];
    if (!file) return;

    const $block = $(this).closest('.block');
    const preview = $block.find('.image-preview')[0];

    // preview локально
    const reader = new FileReader();
    reader.onload = e => {
        preview.src = e.target.result;
        preview.classList.remove('d-none');
    };
    reader.readAsDataURL(file);

    // upload
    uploadImage(file, function(fileName) {
        console.log("UPLOAD SUCCESS, filename:", fileName);
        setImageContent($block, fileName);
    });
});

function uploadImage(file, callback) {
    const formData = new FormData();
    formData.append('image', file);

    // Берём CSRF из meta-тегов (или скрытого input)
    const token = $('input[name="_csrf"]').val(); // или $('meta[name="_csrf"]').attr('content');


    $.ajax({
        url: '/uploads/images',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function(xhr) {
            if (token) xhr.setRequestHeader('X-CSRF-TOKEN', token); // заголовок для CSRF
        },
        success: function(res) {
            console.log("UPLOAD SUCCESS:", res);
            callback(res.filename);
        },
        error: function(xhr) {
            console.error("UPLOAD ERROR:", xhr);
            alert('Ошибка загрузки изображения');
        }
    });
}
function setImageContent($block, fileName) {
    let input = $block.find('input[data-key="content"]');
    if (!input.length) {
        input = $('<input>', { type: 'hidden', 'data-key': 'content' }).appendTo($block);
    }
    input.val(fileName);
}







function previewCover(input) {
    const reader = new FileReader();
    reader.onload = e => {
        coverPreview.src = e.target.result;
        coverPreview.classList.remove('d-none');
        // сброс позиции на центр при загрузке новой картинки
        currentPos = { x: 50, y: 50 };
        coverPreview.style.objectPosition = '50% 50%';
        coverPositionInput.value = '50% 50%';
    };
    reader.readAsDataURL(input.files[0]);
}
// Авторастяжение textarea и contenteditable
function autoResize(el) {
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
}



// Изменение формата в Toolbar текстовый блок
function format(btn, command) {
    const editor = btn.closest('.card-body').querySelector('[contenteditable]');
    if (!editor) return;
    editor.focus();
    document.execCommand(command, false, null);
}

function formatValue(btn, command, value) {
    const editor = btn.closest('.card-body').querySelector('[contenteditable]');
    if (!editor) return;
    editor.focus();

    document.execCommand(command, false, value);
}


function addLink(btn) {
    const editor = btn.closest('.card-body').querySelector('[contenteditable]');
    if (!editor) return;
    editor.focus();
    const url = prompt('URL:');
    if (!url) return;
    document.execCommand('createLink', false, url);
}


// при вводе заголовка он автоматически показывает его в превью и также автоувеличение высоты
$('input[name="title"]').on('input', function () {
    this.style.height = 'auto';
    this.style.height = this.scrollHeight + 'px';
    $('#articleCardTitle').text(this.value || 'Заголовок статьи');
});


function isEmptyHtml(html) {
    if (!html) return true;
    return html.replace(/<br\s*\/?>/gi, '')
        .replace(/&nbsp;/gi, '')
        .trim() === '';
}


$('form').on('submit', function (e) {
    const coverInput = $('input[name="coverFile"]')[0];

    if (!coverInput || coverInput.files.length === 0) {
        alert('Выберите обложку статьи');
        coverInput.classList.add('border', 'border-danger');
        e.preventDefault();
        return;
    }
    const title = $('input[name="title"]').val().trim();
    const slug = $('input[name="slug"]').val().trim();
    const descriptions = $('input[name="descriptions"]').val().trim();
    const altCover = $('input[name="altCover"]').val().trim();

    if (!title) {
        alert('Введите заголовок статьи');
        e.preventDefault();
        return;
    }
    if (!slug) {
        alert('Введите название статьи');
        e.preventDefault();
        return;
    }
    const slugRegex = /^[a-z0-9-]+$/;

    if (!slugRegex.test(slug)) {
        alert('Slug может содержать только латиницу, цифры и дефис');
        e.preventDefault();
        return;
    }
    if (!descriptions) {
        alert('Введите мета описание статьи');
        e.preventDefault();
        return;
    }
    if (!altCover) {
        alert('Введите alt теги обложки');
        e.preventDefault();
        return;
    }
    if (!validateBlocks()) {
        e.preventDefault();
        return;
    }

    $('.block').each(function () {
        saveEditableContent($(this));
    });

    saveBlocksToHiddenInput();
});






new Sortable(document.getElementById('blocksContainer'), {
    animation: 150,
    handle: '.block-drag-handle',
    filter: 'input, textarea, button, [contenteditable]',
    preventOnFilter: false
});


// Block schema:
// {
//   type: 'text' | 'image' | 'quote',
//   content: string | null
// }





const coverPreview = document.getElementById('articleCardCover');
const coverWrapper = document.querySelector('.cover-preview-wrapper');
const coverPositionInput = document.getElementById('coverPosition');

let isDragging = false;
let startX = 0;
let startY = 0;
let currentPos = { x: 50, y: 50 };

// Инициализация позиции из скрытого поля
const initPos = coverPositionInput.value.split(' ');
if(initPos.length === 2){
    currentPos.x = parseFloat(initPos[0]);
    currentPos.y = parseFloat(initPos[1]);
    coverPreview.style.objectPosition = `${currentPos.x}% ${currentPos.y}%`;
}

// Начало drag
coverWrapper.addEventListener('mousedown', e => {
    isDragging = true;
    startX = e.clientX;
    startY = e.clientY;
    coverWrapper.style.cursor = 'grabbing';
});

// Drag
window.addEventListener('mousemove', e => {
    if(!isDragging) return;

    const dx = e.clientX - startX;
    const dy = e.clientY - startY;
    const wrapperRect = coverWrapper.getBoundingClientRect();

    let newX = currentPos.x + (dx / wrapperRect.width) * 100;
    let newY = currentPos.y + (dy / wrapperRect.height) * 100;

    newX = Math.min(100, Math.max(0, newX));
    newY = Math.min(100, Math.max(0, newY));

    // Обновляем визуально
    coverPreview.style.objectPosition = `${newX}% ${newY}%`;

    // **Обновляем hidden input прямо во время drag**
    coverPositionInput.value = `${newX.toFixed(2)}% ${newY.toFixed(2)}%`;
    console.log(coverPositionInput.value);
});

// Конец drag
window.addEventListener('mouseup', e => {
    if(isDragging){
        isDragging = false;
        coverWrapper.style.cursor = 'grab';
        // currentPos уже обновился во время mousemove
    }
});



$(document).ready(function() {
    $('.select-tags').select2({
        tags: true,             // можно добавлять новые теги
        tokenSeparators: [','], // разделение через запятую
        placeholder: "Выберите или добавьте теги"
    });
})


const slugInput = document.getElementById('slugInput');

slugInput.addEventListener('input', function () {
    let v = this.value.toLowerCase();

    // транслитерация RU → LAT (база)
    const map = {
        а:'a', б:'b', в:'v', г:'g', д:'d', е:'e', ё:'e', ж:'zh',
        з:'z', и:'i', й:'y', к:'k', л:'l', м:'m', н:'n', о:'o',
        п:'p', р:'r', с:'s', т:'t', у:'u', ф:'f', х:'h', ц:'c',
        ч:'ch', ш:'sh', щ:'sh', ы:'y', э:'e', ю:'yu', я:'ya'
    };

    v = v.replace(/[а-яё]/g, ch => map[ch] || '');

    // убрать всё кроме a-z 0-9 -
    v = v.replace(/[^a-z0-9-]/g, '');

    // убрать повторные --
    v = v.replace(/-+/g, '-');

    // убрать - в начале и конце
    v = v.replace(/^-|-$/g, '');

    this.value = v;
});


// автогенерация slug по title
$('input[name="title"]').on('input', function () {
    if (!slugInput.value) {
        slugInput.value = this.value
            .toLowerCase()
            .replace(/\s+/g, '-')
            .replace(/[^a-z0-9-]/g, '');
    }
});