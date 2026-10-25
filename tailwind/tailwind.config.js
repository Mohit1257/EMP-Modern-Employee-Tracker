module.exports = {
  prefix: 'tw-',
  darkMode: ['selector', '[data-bs-theme="dark"]'],
  corePlugins: {
    preflight: false
  },
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/js/**/*.js'
  ],
  theme: {
    extend: {}
  },
  plugins: []
};
