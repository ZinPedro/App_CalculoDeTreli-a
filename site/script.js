const header = document.querySelector('.site-header');

window.addEventListener('scroll', () => {
  const raised = window.scrollY > 12;
  header.style.boxShadow = raised ? '0 10px 28px rgba(23, 32, 51, 0.08)' : 'none';
});

document.querySelectorAll('a[href^="#"]').forEach((link) => {
  link.addEventListener('click', (event) => {
    const target = document.querySelector(link.getAttribute('href'));
    if (!target) return;
    event.preventDefault();
    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
  });
});
