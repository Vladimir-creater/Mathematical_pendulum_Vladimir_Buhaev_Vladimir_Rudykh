document.addEventListener('DOMContentLoaded', function() {
    const results = document.querySelector('.results');
    if (results && results.classList.contains('show')) {
        results.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
});