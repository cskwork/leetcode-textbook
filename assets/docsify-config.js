(function configureTraceAtlasDocsify() {
  'use strict';

  var traceAtlasPlugin = window.TraceAtlas && window.TraceAtlas.docsifyPlugin;

  if (typeof traceAtlasPlugin !== 'function') {
    if (typeof window.TraceAtlasDependencyError === 'function') {
      window.TraceAtlasDependencyError('TRACE ATLAS Docsify plugin', true);
    }
    return;
  }

  window.$docsify = {
    name: '<span class="ta-wordmark" aria-label="TRACE ATLAS"><span aria-hidden="true">TA</span> TRACE ATLAS</span>',
    homepage: 'README.md',
    loadSidebar: true,
    alias: {
      '/.*/_sidebar.md': '/_sidebar.md'
    },
    loadNavbar: false,
    coverpage: true,
    onlyCover: false,
    auto2top: true,
    relativePath: false,
    maxLevel: 3,
    subMaxLevel: 3,
    search: {
      placeholder: 'Search patterns and problem lessons',
      noData: 'No matching lessons found',
      depth: 3,
      paths: 'auto',
      hideOtherSidebarContent: false
    },
    copyCode: {
      buttonText: 'Copy',
      errorText: 'Copy failed',
      successText: 'Copied'
    },
    'flexible-alerts': {
      style: 'flat'
    },
    plugins: [traceAtlasPlugin]
  };
}());
