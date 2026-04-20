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





function isEmptyHtml(html) {
    if (!html) return true;
    return html.replace(/<br\s*\/?>/gi, '')
        .replace(/&nbsp;/gi, '')
        .trim() === '';
}


$('form').on('submit', function (e) {


    const title = $('input[name="title"]').val().trim();

    const descriptions = $('input[name="descriptions"]').val().trim();
    const altCover = $('input[name="altCover"]').val().trim();

    if (!title) {
        alert('Введите заголовок статьи');
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



$('input[name="title"]').on('input', function () {
    if (!slugInput.value) {
        slugInput.value = this.value
            .toLowerCase()
            .replace(/\s+/g, '-')
            .replace(/[^a-z0-9-]/g, '');
    }
});