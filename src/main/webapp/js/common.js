/* ============ 公共工具函数 ============ */

/**
 * 自动识别应用上下文路径（兼容部署为根路径 / 或 /sms 等情况）。
 */
function contextPath() {
    const p = location.pathname;
    const markers = ['/login.html', '/register.html', '/admin/', '/teacher/', '/student/'];
    for (const m of markers) {
        const i = p.indexOf(m);
        if (i >= 0) return p.substring(0, i);
    }
    return '';
}

/**
 * 通用请求封装：POST 表单编码方式调用后端 Servlet（BaseServlet 的 method 分发）。
 * 返回 Result 结构 {code, msg, data}。
 */
async function request(url, params) {
    const body = new URLSearchParams();
    for (const k in params) {
        const v = params[k];
        if (v !== undefined && v !== null && v !== '') {
            body.append(k, v);
        }
    }
    const resp = await fetch(contextPath() + url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: body.toString()
    });
    return await resp.json();
}

/** HTML 转义，防止 XSS */
function esc(s) {
    if (s === null || s === undefined) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/** 显示提示信息 */
function showMsg(id, type, text) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = text;
    el.className = 'msg ' + (type === 'ok' ? 'ok' : 'err');
    setTimeout(() => { el.className = 'msg'; }, 3000);
}

/** 退出登录 */
async function logout() {
    await request('/user', { method: 'logout' });
    location.href = contextPath() + '/login.html';
}

/** 获取当前登录用户信息（带缓存） */
let _userCache = null;
async function currentUser() {
    if (_userCache) return _userCache;
    const r = await request('/user', { method: 'info' });
    if (r.code === 0) _userCache = r.data;
    return _userCache;
}

/** 页面加载时初始化顶栏用户信息 */
async function initTopbar() {
    const u = await currentUser();
    if (!u) return;
    const el = document.querySelector('#helloUser');
    if (el) {
        const roleText = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }[u.role] || u.role;
        el.innerHTML = '欢迎，<b>' + esc(u.name || u.username) + '</b>（' + roleText + '）';
    }
}
