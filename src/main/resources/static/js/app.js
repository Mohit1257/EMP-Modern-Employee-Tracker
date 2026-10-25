/**
 * EMP- Modern Employee Tracker
 * Client Interactions, Theme Controller & Chart Handlers
 * Attribution: Built by Mohit Shinde
 */

(function () {
  'use strict';

  // 1. Theme Management (Dark / Light)
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  const themeIcon = document.getElementById('themeIcon');

  function applyTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    localStorage.setItem('emptrack_theme', theme);
    if (themeIcon) {
      themeIcon.className = theme === 'dark' ? 'bi bi-sun-fill text-warning' : 'bi bi-moon-stars text-secondary';
    }
  }

  const savedTheme = localStorage.getItem('emptrack_theme') || 'light';
  applyTheme(savedTheme);

  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', () => {
      const current = document.documentElement.getAttribute('data-bs-theme') || 'light';
      applyTheme(current === 'dark' ? 'light' : 'dark');
    });
  }

  // 2. Mobile Sidebar Toggle
  const sidebarToggle = document.getElementById('sidebarToggle');
  const appSidebar = document.querySelector('.app-sidebar');

  if (sidebarToggle && appSidebar) {
    sidebarToggle.addEventListener('click', () => {
      appSidebar.classList.toggle('show');
    });

    document.addEventListener('click', (e) => {
      if (window.innerWidth < 992 && !appSidebar.contains(e.target) && !sidebarToggle.contains(e.target)) {
        appSidebar.classList.remove('show');
      }
    });
  }

  // 3. Confirm Delete Modals
  const confirmDeleteModal = document.getElementById('confirmDeleteModal');
  if (confirmDeleteModal) {
    confirmDeleteModal.addEventListener('show.bs.modal', function (event) {
      const button = event.relatedTarget;
      const deleteUrl = button.getAttribute('data-delete-url');
      const itemName = button.getAttribute('data-item-name');
      const itemType = button.getAttribute('data-item-type') || 'item';

      const deleteForm = document.getElementById('deleteModalForm');
      const deleteMessage = document.getElementById('deleteModalMessage');

      if (deleteForm && deleteUrl) {
        deleteForm.action = deleteUrl;
      }
      if (deleteMessage && itemName) {
        deleteMessage.innerHTML = `Are you sure you want to delete ${itemType} <strong>"${itemName}"</strong>? This action cannot be undone.`;
      }
    });
  }

  // 4. Fallbacks when the Bootstrap JavaScript CDN file cannot be loaded
  if (typeof bootstrap === 'undefined') {
    document.querySelectorAll('[data-bs-toggle="dropdown"]').forEach((toggle) => {
      toggle.addEventListener('click', (e) => {
        e.stopPropagation();
        const menu = toggle.parentElement.querySelector('.dropdown-menu');
        if (menu) {
          menu.classList.toggle('show');
        }
      });
    });

    document.querySelectorAll('[data-delete-url]').forEach((button) => {
      button.addEventListener('click', () => {
        const itemName = button.getAttribute('data-item-name') || 'this record';
        const deleteForm = document.getElementById('deleteModalForm');
        if (deleteForm && window.confirm('Delete "' + itemName + '"? This action cannot be undone.')) {
          deleteForm.action = button.getAttribute('data-delete-url');
          deleteForm.submit();
        }
      });
    });
  }
})();
