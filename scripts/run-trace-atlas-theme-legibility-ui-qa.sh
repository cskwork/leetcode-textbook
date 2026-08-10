#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

base_url="http://127.0.0.1:8765"

for asset in index.html assets/atlas-theme.css assets/atlas-shell.js; do
  local_hash="$(shasum -a 256 "$asset" | awk '{print $1}')"
  served_hash="$(curl -fsS "$base_url/$asset" | shasum -a 256 | awk '{print $1}')"
  if [[ "$local_hash" != "$served_hash" ]]; then
    echo "Served asset does not match this worktree: $asset" >&2
    exit 1
  fi
done

ego-browser nodejs <<'EGO'
const task = await useOrCreateTaskSpace('trace atlas theme legibility qa')
const base = 'http://127.0.0.1:8765/'
const lessonRoute = '#/patterns/01-arrays-hashing/0001-two-sum/README'
const cases = [[1440, 900, 'dark'], [1440, 900, 'light'], [390, 844, 'dark'], [390, 844, 'light']]
let nonce = 1

function check(condition, message) {
  if (!condition) throw new Error(message)
}

async function viewport(width, height) {
  await cdp('Emulation.setDeviceMetricsOverride', { width, height, deviceScaleFactor: 1, mobile: false })
}

async function fresh(route) {
  await gotoAndWait(base + '?ego-qa=' + nonce++ + route, { timeout: 20, settle: 0.6 })
}

async function ensureTheme(theme, route) {
  await fresh(route)
  if (await js(`document.documentElement.dataset.taTheme`) !== theme) {
    await click('.ta-theme-toggle', { label: 'set QA theme' })
    await wait(0.35)
  }
  await fresh(route)
  check(await js(`document.documentElement.dataset.taTheme`) === theme, 'theme did not persist after reload')
}

const contrastExpression = String.raw`(() => {
  const parse = value => { const match = value.match(/[\d.]+/g); if (!match) return [0,0,0,0]; const n = match.map(Number); return [n[0],n[1],n[2],n.length > 3 ? n[3] : 1] }
  const over = (fg,bg) => { const a = fg[3] + bg[3] * (1 - fg[3]); return [(fg[0]*fg[3] + bg[0]*bg[3]*(1-fg[3]))/a,(fg[1]*fg[3] + bg[1]*bg[3]*(1-fg[3]))/a,(fg[2]*fg[3] + bg[2]*bg[3]*(1-fg[3]))/a,a] }
  const channel = c => { const v = c / 255; return v <= 0.04045 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4) }
  const ratio = (a,b) => { const x=.2126*channel(a[0])+.7152*channel(a[1])+.0722*channel(a[2]), y=.2126*channel(b[0])+.7152*channel(b[1])+.0722*channel(b[2]); return (Math.max(x,y)+.05)/(Math.min(x,y)+.05) }
  const background = el => { const chain=[]; for(let node=el;node;node=node.parentElement) chain.unshift(node); let bg=[255,255,255,1]; for(const node of chain) bg=over(parse(getComputedStyle(node).backgroundColor),bg); return bg }
  const visible = el => { const s=getComputedStyle(el), r=el.getBoundingClientRect(); return s.display!=='none' && s.visibility!=='hidden' && Number(s.opacity)>0 && r.width>0 && r.height>0 && r.bottom>0 && r.right>0 && r.top<innerHeight && r.left<innerWidth }
  const failures=[]; let count=0, minimum=999
  for(const el of document.querySelectorAll('body *')) {
    if(!visible(el) || ![...el.childNodes].some(node => node.nodeType===3 && node.nodeValue.trim())) continue
    count += 1
    const style=getComputedStyle(el), bg=background(el), fg=over(parse(style.color),bg), value=ratio(fg,bg), size=parseFloat(style.fontSize), weight=parseInt(style.fontWeight)||400
    const threshold=(size>=24 || (size>=18.66 && weight>=700)) ? 3 : 4.5
    minimum=Math.min(minimum,value)
    if(value+0.01<threshold) failures.push({text:el.textContent.trim().slice(0,60),ratio:+value.toFixed(2),threshold})
  }
  return {count,minimum:+minimum.toFixed(2),failures,overflow:document.documentElement.scrollWidth-innerWidth}
})()`

async function contrast(label) {
  const result = await js(contrastExpression)
  check(result.count > 0, label + ': no visible text samples')
  check(result.failures.length === 0, label + ': contrast failures ' + JSON.stringify(result.failures.slice(0, 4)))
  check(result.overflow <= 0, label + ': document overflow ' + result.overflow)
  return result.minimum
}

async function lessonGeometry(label) {
  const result = await js(String.raw`(() => {
    const intersects=(a,b)=>a.right>b.left&&a.left<b.right&&a.bottom>b.top&&a.top<b.bottom
    const overlaps=[]
    document.querySelectorAll('.docsify-copy-code-button').forEach((button,blockIndex)=>{
      const buttonRect=button.getBoundingClientRect(), code=button.parentElement && button.parentElement.querySelector('code')
      if(!code) return
      const walker=document.createTreeWalker(code,NodeFilter.SHOW_TEXT); let node
      while((node=walker.nextNode())) {
        if(!node.nodeValue || !node.nodeValue.trim()) continue
        const range=document.createRange(); range.selectNodeContents(node)
        for(const rect of range.getClientRects()) if(rect.width&&rect.height&&intersects(rect,buttonRect)) overlaps.push({blockIndex,text:node.nodeValue.trim().slice(0,24)})
      }
    })
    const rowColors=[...document.querySelectorAll('table tbody tr')].map(row=>getComputedStyle(row).backgroundColor)
    return {statuses:document.querySelectorAll('.ta-status-button').length,stations:document.querySelectorAll('.ta-station-link').length,copyButtons:document.querySelectorAll('.docsify-copy-code-button').length,tables:document.querySelectorAll('table').length,adjacent:document.querySelectorAll('.ta-lesson-nav a').length,overlaps,rowColors,overflow:document.documentElement.scrollWidth-innerWidth}
  })()`)
  check(result.statuses === 3, label + ': expected three statuses')
  check(result.stations === 5, label + ': expected five stations')
  check(result.copyButtons > 0 && result.tables === 1 && result.adjacent === 2, label + ': lesson controls missing')
  check(result.overlaps.length === 0, label + ': rendered code overlaps copy control')
  check(new Set(result.rowColors).size >= 2, label + ': table rows are not visibly alternating')
  check(result.overflow <= 0, label + ': lesson overflow')
}

await cdp('Runtime.enable')
await cdp('Log.enable')
await cdp('Network.enable')

const matrix=[]
for (const [width,height,theme] of cases) {
  await viewport(width,height)
  await ensureTheme(theme,'#/')
  const rootMinimum=await contrast(`${width}x${height} ${theme} root`)
  const root=await js(`({title:document.querySelector('.ta-dashboard h2')?.textContent||document.querySelector('h1')?.textContent,cards:document.querySelectorAll('.ta-pattern-card').length,overflow:document.documentElement.scrollWidth-innerWidth})`)
  check(root.title && root.cards===16 && root.overflow<=0, 'cover/dashboard contract failed')

  await fresh(lessonRoute)
  await wait(0.6)
  const lessonMinimum=await contrast(`${width}x${height} ${theme} lesson`)
  await lessonGeometry(`${width}x${height} ${theme}`)

  const copy='pre.language-java .docsify-copy-code-button'
  await js(`document.querySelector('${copy}').focus()`); await pressKey('Enter'); await wait(0.2)
  check(await js(`document.querySelector('${copy} [aria-live]').textContent.trim()==='Copied'`), 'copy did not reach its Copied live state')

  if(width===390) { await js(`document.querySelector('button[aria-label="Menu"]').focus()`); await pressKey('Enter'); await wait(0.2) }
  await fillInput('input[placeholder="Search patterns and problem lessons"]','Two Sum')
  await wait(0.7)
  const search=await js(`({results:document.querySelectorAll('.matching-post').length,marks:document.querySelectorAll('.search-keyword').length,exact:!!document.querySelector('.matching-post a[href*="0001-two-sum/README"]'),overflow:document.documentElement.scrollWidth-innerWidth})`)
  check(search.results>0 && search.marks>0 && search.exact && search.overflow<=0, 'search contract failed')
  await click('.matching-post a[href*="0001-two-sum/README"]',{label:'open exact Two Sum'})
  await wait(0.5)
  check(await js(`location.hash.split('?')[0]`)===lessonRoute, 'search result did not open canonical Two Sum route')

  await fresh(lessonRoute)
  await js(`document.querySelector('.ta-walkthrough-button').focus()`); await pressKey('Enter'); await wait(0.2)
  check(await js(`window.top.document.querySelector('.ta-dialog').contains(window.top.document.activeElement)`), 'dialog did not receive focus')
  for(let index=0; index<20; index+=1) {
    await pressKey('Tab'); await wait(0.05)
  }
  await wait(0.5)
  check(await js(`window.top.document.querySelector('.ta-dialog').contains(window.top.document.activeElement)`), 'dialog focus escaped after repeated Tab traversal')
  await pressKey('Escape'); await wait(0.1)
  check(await js(`!window.top.document.querySelector('.ta-dialog') && window.top.document.activeElement===window.top.document.querySelector('.ta-walkthrough-button')`), 'dialog did not close and restore focus')
  matrix.push({width,height,theme,rootMinimum,lessonMinimum})
}

await viewport(1440,900)
await ensureTheme('dark',lessonRoute)
await click('.ta-status-button[data-status="studying"]',{label:'set Studying status'})
const checkpointOpen=await js(`document.querySelector('details[data-ta-checkpoint]').open`)
if(!checkpointOpen) await click('details[data-ta-checkpoint] > summary',{label:'reveal checkpoint'})
const patternSlug=await js(`document.querySelectorAll('.ta-pattern-toggle')[1].dataset.pattern`)
await click('.ta-pattern-toggle[data-pattern="'+patternSlug+'"]',{label:'change pattern expansion'})
const expectedExpansion=await js(`document.querySelector('.ta-pattern-toggle[data-pattern="${patternSlug}"]').getAttribute('aria-expanded')`)
await click('.ta-theme-toggle',{label:'persist changed theme'}); await wait(0.35)
const expectedTheme=await js(`document.documentElement.dataset.taTheme`)
await fresh(lessonRoute)
const persisted=await js(`({theme:document.documentElement.dataset.taTheme,studying:document.querySelector('.ta-status-button[data-status="studying"]').getAttribute('aria-pressed'),completed:document.querySelector('.ta-status-button[data-status="completed"]').getAttribute('aria-pressed'),revealed:document.querySelector('details[data-ta-checkpoint]').classList.contains('ta-checkpoint--revealed'),expanded:document.querySelector('.ta-pattern-toggle[data-pattern="${patternSlug}"]').getAttribute('aria-expanded'),route:location.hash})`)
check(persisted.theme===expectedTheme && persisted.studying==='true' && persisted.completed==='false' && persisted.revealed && persisted.expanded===expectedExpansion && persisted.route===lessonRoute,'independent state did not persist')
const collapsed=await js(`[...document.querySelectorAll('.ta-pattern-toggle')].filter(button=>button.getAttribute('aria-expanded')!=='true').map(button=>button.dataset.pattern)`)
for(const slug of collapsed) await click('.ta-pattern-toggle[data-pattern="'+slug+'"]',{label:'expand pattern group'})
const curriculum=await js(String.raw`(() => {const groups=[...document.querySelectorAll('.ta-pattern-toggle')],links=[...document.querySelectorAll('.ta-pattern-group__list a[data-lesson-status]')],destinations=links.map(a=>a.getAttribute('href').replace(/^#\//,'').split(/[?#]/)[0]);return{groups:groups.length,ordered:groups.every((button,index)=>button.textContent.includes('Pattern '+(index+1)+':')),expanded:groups.every(button=>button.getAttribute('aria-expanded')==='true'),links:links.length,unique:new Set(destinations).size}})()`)
check(curriculum.groups===16 && curriculum.ordered && curriculum.expanded && curriculum.links===100 && curriculum.unique===100,'curriculum count/order contract failed')

const transitions=[]
for(const [width,height] of [[1440,900],[390,844]]) {
  await viewport(width,height)
  for(const route of ['#/',lessonRoute]) {
    for(const [from,to] of [['dark','light'],['light','dark']]) {
      await ensureTheme(from,route)
      await click('.ta-theme-toggle',{label:'sample theme transition'})
      const frames=[]
      let previous=0
      for(const target of [0,70,160,300]) {
        if(target>previous) await wait((target-previous)/1000)
        previous=target
        const minimum=await contrast(`${width}x${height} ${route} ${from} to ${to} at ${target}ms`)
        const state=await js(`({theme:document.documentElement.dataset.taTheme,dark:document.documentElement.classList.contains('dark-mode'),scheme:getComputedStyle(document.documentElement).colorScheme,pressed:document.querySelector('.ta-theme-toggle').getAttribute('aria-pressed')})`)
        check(state.theme===to && state.dark===(to==='dark') && state.scheme.includes(to) && state.pressed===(to==='dark'?'true':'false'),'theme surfaces disagreed during transition')
        frames.push({targetMs:target,minimum})
      }
      await fresh(route)
      check(await js(`document.documentElement.dataset.taTheme`)===to,'transition theme did not persist')
      transitions.push({width,height,route,from,to,frames})
    }
  }
}

await cdp('Emulation.setEmulatedMedia',{media:'screen',features:[{name:'prefers-reduced-motion',value:'reduce'}]})
const reduced=[]
for(const [width,height,theme] of cases) {
  await viewport(width,height); await ensureTheme(theme,lessonRoute)
  await pressKey('Tab')
  const skip=await js(`({isSkip:document.activeElement.classList.contains('ta-skip-link'),outline:getComputedStyle(document.activeElement).outlineWidth})`)
  await pressKey('Enter'); await wait(0.05)
  const reached=await js(`location.hash===${JSON.stringify(lessonRoute)} && document.activeElement===document.querySelector('#main-content')`)
  await js(`document.querySelector('.ta-theme-toggle').focus()`); await pressKey('Enter'); await wait(0.05)
  await js(`document.querySelector('details[data-ta-checkpoint] > summary').focus()`); await pressKey('Enter'); await wait(0.05)
  if(width===390) {
    await js(`document.querySelector('button[aria-label="Menu"]').focus()`); await pressKey('Enter'); await wait(0.05)
    check(await js(`document.querySelector('.sidebar').getBoundingClientRect().x`)>=-1,'reduced-motion sidebar did not open')
    await js(`document.querySelector('button[aria-label="Menu"]').focus()`); await pressKey('Enter'); await wait(0.05)
  } else {
    await js(`document.querySelectorAll('.ta-pattern-toggle')[1].focus()`); await pressKey('Enter'); await wait(0.05)
  }
  await js(`document.querySelector('.ta-walkthrough-button').focus()`); await pressKey('Enter'); await wait(0.2)
  await pressKey('Tab'); await wait(0.15)
  check(await js(`window.top.document.querySelector('.ta-dialog').contains(window.top.document.activeElement)`),'reduced-motion dialog focus escaped')
  await pressKey('Escape'); await wait(0.05)
  check(await js(`!window.top.document.querySelector('.ta-dialog') && window.top.document.activeElement===window.top.document.querySelector('.ta-walkthrough-button')`),'reduced-motion dialog did not restore focus')
  const motion=await js(String.raw`(() => {const times=v=>v.split(',').map(x=>x.trim().endsWith('ms')?parseFloat(x):parseFloat(x)*1000);let max=0;for(const el of document.querySelectorAll('*')){const s=getComputedStyle(el);max=Math.max(max,...times(s.transitionDuration),...times(s.transitionDelay),...times(s.animationDuration),...times(s.animationDelay))}return{reduce:matchMedia('(prefers-reduced-motion: reduce)').matches,max,overflow:document.documentElement.scrollWidth-innerWidth}})()`)
  check(skip.isSkip && parseFloat(skip.outline)>0 && reached && motion.reduce && motion.max<=16.7 && motion.overflow<=0,'reduced-motion keyboard contract failed')
  reduced.push({width,height,theme,maxMs:motion.max})
}

const events=await drainEvents()
const errors=events.filter(event => event.method==='Runtime.exceptionThrown' || (event.method==='Log.entryAdded' && event.params?.entry?.level==='error') || (event.method==='Network.loadingFailed' && !event.params?.canceled))
check(errors.length===0,'browser error/resource stream is not clean: '+JSON.stringify(errors.slice(0,5)))
cliLog(JSON.stringify({taskSpace:task.id,assetHashes:'matched',matrix,persistence:persisted,curriculum,transitions,reduced,browserErrors:errors.length,pass:true},null,2))
EGO

ego-browser nodejs <<'EGO'
const result = await completeTaskSpace('trace atlas theme legibility qa', { keep: false })
if (!result.done) throw new Error('Ego task-space cleanup did not complete: ' + JSON.stringify(result))
cliLog(JSON.stringify(result))
EGO
