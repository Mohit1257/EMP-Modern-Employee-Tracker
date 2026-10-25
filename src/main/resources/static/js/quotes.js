(function () {
  'use strict';

  const quotes = [
    'Great teams are built on clear goals, honest feedback and mutual trust.',
    'Good workforce data turns busy days into better decisions.',
    'Every role matters when people can see how their work connects.',
    'Productivity grows where people feel valued and supported.',
    'Small, consistent improvements compound into remarkable results.',
    'Teamwork turns individual strengths into shared achievements.',
    'Clear communication saves more time than any shortcut.',
    'Organised records give managers more time to support their people.'
  ];

  const elements = document.querySelectorAll('[data-quote]');
  if (elements.length === 0) {
    return;
  }

  const dayOfYear = Math.floor((Date.now() - new Date(new Date().getFullYear(), 0, 0)) / 86400000);
  const quote = quotes[dayOfYear % quotes.length];

  elements.forEach(function (element) {
    element.textContent = quote;
  });
})();
