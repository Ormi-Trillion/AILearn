// SweetAlert2 라이브러리 추가
document.addEventListener('DOMContentLoaded', function() {
    loadScript('https://cdn.jsdelivr.net/npm/sweetalert2@11', initializeApp);
});

function loadScript(url, callback) {
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onload = callback;
    document.head.appendChild(script);
}

function initializeApp() {
    let currentPage = 0;
    const pageSize = 10; // 페이지당 아이템 수

    loadVocabularies(currentPage);

    // 단어 추가 버튼 이벤트 리스너
    document.getElementById('addVocabularyButton').addEventListener('click', addVocabulary);

async function loadVocabularies(page) {
    try {
        const response = await fetch(`/api/me/vocabulary-list?page=${page}&size=${pageSize}`);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();
        renderVocabularies(data.content);
        renderPagination(data);
    } catch (error) {
        console.error('Error fetching vocabularies:', error);
        showAlert('오류', '단어장을 불러오는 중 오류가 발생했습니다.', 'error');
    }
}

function renderVocabularies(vocabularies) {
    const container = document.getElementById('vocabularyContainer');
    container.innerHTML = '';
    vocabularies.forEach(voca => {
        const vocaElement = document.createElement('div');
        vocaElement.className = 'vocabulary';
        vocaElement.innerHTML = `
            <div class="vocabulary-header">
                <span>${voca.word} : ${voca.meaning}</span>
                <button class="delete-vocabulary" data-id="${voca.id}"><img src="/images/x-mark.png" alt="단어 삭제 버튼" title="단어 삭제 버튼"></button>
            </div>
            <p class="vocabulary-content">예문 : ${voca.exampleSentence}</p>
        `;
        container.appendChild(vocaElement);
    });

    // 삭제 버튼에 이벤트 리스너 추가
    document.querySelectorAll('.delete-vocabulary').forEach(button => {
        button.addEventListener('click', function() {
            const vocaId = this.getAttribute('data-id');
            confirmDeleteVocabulary(vocaId);
        });
    });
}

function renderPagination(pageData) {
    const container = document.getElementById('paginationContainer');
    container.innerHTML = '';

    for (let i = 0; i < pageData.totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.innerText = i + 1;
        pageButton.addEventListener('click', () => {
            currentPage = i;
            loadVocabularies(currentPage);
        });
        if (i === pageData.number) {
            pageButton.disabled = true;
        }
        container.appendChild(pageButton);
    }
}

function confirmDeleteVocabulary(id) {
    Swal.fire({
        title: '정말로 삭제 하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '예',
        cancelButtonText: '아니오'
    }).then((result) => {
        if (result.isConfirmed) {
            deleteVocabulary(id);
        }
    });
}

async function deleteVocabulary(id) {
    try {
        const response = await fetch(`/api/vocabulary-list/me/vocabularies/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        showAlert('성공', '단어가 삭제되었습니다.', 'success');
        loadVocabularies(currentPage); // 현재 페이지 새로고침
    } catch (error) {
        console.error('Error deleting vocabulary:', error);
        showAlert('오류', '단어 삭제 중 오류가 발생했습니다.', 'error');
    }
}

async function addVocabulary() {
    try {
        const response = await fetch('/api/vocabulary-list/me/vocabularies', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        showAlert('성공', '단어가 추가되었습니다.', 'success');
        loadVocabularies(currentPage); // 현재 페이지 새로고침
    } catch (error) {
        console.error('Error adding vocabulary:', error);
        showAlert('오류', '단어 추가 중 오류가 발생했습니다.', 'error');
    }
}

async function submitVocabulary(word, meaning, exampleSentence) {
    try {
        const response = await fetch('/api/vocabulary-list/me/vocabularies', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ word, meaning, exampleSentence }),
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        showAlert('성공', '단어가 추가되었습니다.', 'success');
        loadVocabularies(currentPage); // 현재 페이지 새로고침
    } catch (error) {
        console.error('Error adding vocabulary:', error);
        showAlert('오류', '단어 추가 중 오류가 발생했습니다.', 'error');
    }
}

function showAlert(title, text, icon) {
    Swal.fire({
        title: title,
        text: text,
        icon: icon
    });
}}