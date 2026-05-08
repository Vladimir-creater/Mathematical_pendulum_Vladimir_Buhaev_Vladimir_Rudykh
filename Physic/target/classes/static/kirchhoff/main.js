'use strict';

/* ── Tabs ────────────────────────────────────────────────── */
function showTab(name) {
    document.querySelectorAll('.section-card').forEach(el => el.classList.remove('visible'));
    document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
    document.getElementById('section-' + name).classList.add('visible');
    document.getElementById('btn-' + name).classList.add('active');
    sessionStorage.setItem('activeTab', name);
}

/* ── Read value: SVG input first, then textbox ───────────── */
function read(svgId, tbId) {
    const sv = document.getElementById(svgId);
    const tb = document.getElementById(tbId);
    if (sv && sv.value.trim() !== '') return sv.value.trim();
    if (tb && tb.value.trim() !== '') return tb.value.trim();
    return '';
}

/* ── Mirror: SVG ↔ textbox ───────────────────────────────── */
function mirror(svgId, tbId, cb) {
    const sv = document.getElementById(svgId);
    const tb = document.getElementById(tbId);
    if (sv) sv.addEventListener('input', () => { if (tb) tb.value = sv.value; if(cb) cb(); });
    if (tb) tb.addEventListener('input', () => { if (sv) sv.value = tb.value; if(cb) cb(); });
}

/* ── Live: First law ─────────────────────────────────────── */
function liveFirst() {
    const i1 = parseFloat(document.getElementById('fi-i1')?.value) || 0;
    const i2 = parseFloat(document.getElementById('fi-i2')?.value) || 0;
    const i3 = parseFloat(document.getElementById('fi-i3')?.value) || 0;
    const el = document.getElementById('svg1-iout');
    if (!el) return;
    const any = ['fi-i1','fi-i2','fi-i3'].some(id => document.getElementById(id)?.value !== '');
    el.textContent = any ? '= ' + (i1+i2+i3).toFixed(3) + ' А' : '= ?';
}

/* ── Live: Second law — E → (R1∥R2) → R3 ────────────────── */
function liveSecond() {
    const emf = parseFloat(document.getElementById('fi-emf')?.value) || 0;
    const r1  = parseFloat(document.getElementById('fi-r1')?.value)  || 0;
    const r2  = parseFloat(document.getElementById('fi-r2')?.value)  || 0;
    const r3  = parseFloat(document.getElementById('fi-r3')?.value)  || 0;

    const rPar   = (r1 + r2 > 1e-12) ? (r1 * r2) / (r1 + r2) : 0;
    const rTotal = rPar + r3;
    const iTotal = (rTotal > 1e-12) ? emf / rTotal : 0;
    const uPar   = iTotal * rPar;
    const u3     = iTotal * r3;
    const i1     = (r1 > 1e-12) ? uPar / r1 : 0;
    const i2     = (r2 > 1e-12) ? uPar / r2 : 0;

    const any = emf || r1 || r2 || r3;
    function upd(id, val, unit, dec) {
        const el = document.getElementById(id);
        if (el) el.textContent = any ? val.toFixed(dec) + unit : '?';
    }
    upd('svg2-rpar',   rPar,   ' Ом', 3);
    upd('svg2-rtotal', rTotal, ' Ом', 3);
    upd('svg2-itotal', iTotal, ' А',  5);
    upd('svg2-upar',   uPar,   ' В',  3);
    upd('svg2-u3',     u3,     ' В',  3);
    const el12 = document.getElementById('svg2-i12');
    if (el12) el12.textContent = any
        ? i1.toFixed(5) + ' / ' + i2.toFixed(5) + ' А' : '?';
}

/* ── Prepare & validate ──────────────────────────────────── */
function prepareFirst() {
    const map = [['h-i1','fi-i1','tb-i1'],['h-i2','fi-i2','tb-i2'],['h-i3','fi-i3','tb-i3']];
    for (const [h, f, t] of map) {
        document.getElementById(h).value = read(f, t);
        if (!document.getElementById(h).value) {
            alert('Заполни все три тока'); return false;
        }
    }
    const d = document.getElementById('tb-deltaI');
    const hd = document.getElementById('h-deltaI');
    if (d && hd) hd.value = d.value || '0.05';
    return true;
}

function prepareSecond() {
    const map = [
        ['h-emf','fi-emf','tb-emf'],
        ['h-r1', 'fi-r1', 'tb-r1'],
        ['h-r2', 'fi-r2', 'tb-r2'],
        ['h-r3', 'fi-r3', 'tb-r3'],
    ];
    for (const [h, f, t] of map) {
        document.getElementById(h).value = read(f, t);
        if (!document.getElementById(h).value) {
            alert('Заполни все поля (E, R₁, R₂, R₃)'); return false;
        }
    }
    [['h-deltaE','tb-deltaE','0.1'],['h-deltaR','tb-deltaR','0.5']].forEach(([h,t,def]) => {
        const hEl = document.getElementById(h);
        const tEl = document.getElementById(t);
        if (hEl) hEl.value = (tEl && tEl.value) ? tEl.value : def;
    });
    return true;
}

/* ── Animate number ──────────────────────────────────────── */
function animateNum(el, to, unit) {
    const t0 = performance.now(), dur = 650;
    (function tick(now) {
        const p = Math.min((now - t0) / dur, 1);
        const e = 1 - Math.pow(1 - p, 3);
        el.textContent = (to * e).toFixed(4) + unit;
        if (p < 1) requestAnimationFrame(tick);
    })(t0);
}

/* ── Init ────────────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
    if      (document.getElementById('second-result-box')) showTab('second');
    else if (document.getElementById('first-result-box'))  showTab('first');
    else showTab(sessionStorage.getItem('activeTab') || 'first');

    // First law mirrors
    mirror('fi-i1','tb-i1', liveFirst);
    mirror('fi-i2','tb-i2', liveFirst);
    mirror('fi-i3','tb-i3', liveFirst);

    // Second law mirrors
    mirror('fi-emf','tb-emf', liveSecond);
    mirror('fi-r1', 'tb-r1',  liveSecond);
    mirror('fi-r2', 'tb-r2',  liveSecond);
    mirror('fi-r3', 'tb-r3',  liveSecond);

    liveFirst();
    liveSecond();

    // Animate highlighted results
    document.querySelectorAll('.result-value[data-val]').forEach(el => {
        const v = parseFloat(el.getAttribute('data-val'));
        const u = el.getAttribute('data-unit') || '';
        if (!isNaN(v)) animateNum(el, v, u);
    });
});
