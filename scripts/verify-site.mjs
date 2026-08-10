#!/usr/bin/env node

import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { relative } from 'node:path';
import { runInNewContext } from 'node:vm';

const root = new URL('../', import.meta.url);
const read = (path) => readFileSync(new URL(path, root), 'utf8');
const fail = (message) => {
  console.error(`FAIL: ${message}`);
  process.exitCode = 1;
};
const pass = (message) => console.log(`PASS: ${message}`);

const requiredFiles = [
  'index.html',
  '_coverpage.md',
  '_sidebar.md',
  'assets/atlas-theme.css',
  'assets/atlas-shell.js',
  'assets/docsify-config.js',
  'assets/walkthrough-manifest.js',
];

for (const path of requiredFiles) {
  if (existsSync(new URL(path, root))) pass(`${path} exists`);
  else fail(`${path} is missing`);
}

const sidebar = read('_sidebar.md');
const lessonRoutes = [...sidebar.matchAll(/\]\((patterns\/[^/)]+\/[^/)]+\/README)\)/g)].map((match) => match[1]);
const uniqueRoutes = new Set(lessonRoutes);

if (lessonRoutes.length === 100) pass('sidebar contains 100 ordered Problem Lesson routes');
else fail(`expected 100 sidebar Problem Lesson routes, found ${lessonRoutes.length}`);

if (uniqueRoutes.size === lessonRoutes.length) pass('Problem Lesson route identities are unique');
else fail('duplicate Problem Lesson routes found in sidebar');

for (const route of uniqueRoutes) {
  const lessonPath = `${route}.md`;
  if (!existsSync(new URL(lessonPath, root))) fail(`missing lesson file for ${route}`);
}

const patternsDir = new URL('patterns/', root);
const visualizationPaths = [];
for (const pattern of readdirSync(patternsDir, { withFileTypes: true })) {
  if (!pattern.isDirectory()) continue;
  const patternDir = new URL(`${pattern.name}/`, patternsDir);
  for (const problem of readdirSync(patternDir, { withFileTypes: true })) {
    if (!problem.isDirectory()) continue;
    const visualization = new URL(`${problem.name}/visualization/index.html`, patternDir);
    if (existsSync(visualization)) {
      visualizationPaths.push(relative(new URL('.', root).pathname, visualization.pathname));
    }
  }
}

const manifestContext = { window: {}, Object };
runInNewContext(read('assets/walkthrough-manifest.js'), manifestContext);
const manifestRoutes = Array.from(manifestContext.window.TraceAtlasWalkthroughRoutes || []);
const discoveredRoutes = visualizationPaths.map((path) => path.replace(/\/visualization\/index\.html$/, ''));
const unknownManifestRoutes = manifestRoutes.filter((route) => !uniqueRoutes.has(`${route}/README`));
const manifestMismatch = manifestRoutes.length !== discoveredRoutes.length
  || manifestRoutes.some((route) => !discoveredRoutes.includes(route));

if (!manifestMismatch) pass(`walkthrough manifest matches all ${visualizationPaths.length} discovered files`);
else fail('walkthrough manifest and discovered visualization files do not match');

if (!unknownManifestRoutes.length) pass('every walkthrough belongs to a real Problem Lesson route');
else fail(`walkthrough manifest has unknown lesson routes: ${unknownManifestRoutes.join(', ')}`);

const html = read('index.html');
for (const asset of ['assets/atlas-theme.css', 'assets/walkthrough-manifest.js', 'assets/atlas-shell.js', 'assets/docsify-config.js']) {
  if (html.includes(asset)) pass(`index.html loads ${asset}`);
  else fail(`index.html does not load ${asset}`);
}

if (/class=["'][^"']*ta-skip-link\b/.test(html)) pass('skip navigation is present');
else fail('skip navigation is missing');

const shell = existsSync(new URL('assets/atlas-shell.js', root)) ? read('assets/atlas-shell.js') : '';
if (shell.includes('trace-atlas:v1:learner-state')) pass('learner-state storage is explicitly versioned/namespaced');
else fail('learner-state storage namespace is missing');

if (/status\s*[:=]/.test(shell) && /completed/.test(shell) && /studying/.test(shell)) {
  pass('explicit Lesson Status states are implemented');
} else {
  fail('explicit Lesson Status states are incomplete');
}

if (process.exitCode) process.exit(process.exitCode);
console.log(`\nSite contract verified: ${lessonRoutes.length} lessons, ${visualizationPaths.length} walkthroughs.`);
