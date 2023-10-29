const CLASS_NAME_SHOW = 'show';
const ATTRIBUTE_NAME_POPPER = 'data-bs-popper';

/** @type {(classList: DOMTokenList) => Array<string>} */
const filterIconClassList = (classList) => {
    return Array.from(classList.values()).filter(c => c.startsWith('fa-') && c !== 'fa-solid')
};
const getStoredTheme = () => localStorage.getItem('theme');
const setStoredTheme = theme => localStorage.setItem('theme', theme);

const getPreferredTheme = () => {
    const storedTheme = getStoredTheme();
    if (storedTheme) {
        return storedTheme;
    }

    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

const setTheme = theme => {
    if (theme === 'auto' && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        document.documentElement.setAttribute('data-bs-theme', 'dark');
    } else {
        document.documentElement.setAttribute('data-bs-theme', theme);
    }
};

setTheme(getPreferredTheme())

const showActiveTheme = (theme, focus = false) => {
    const themeSwitcher = document.querySelector('#bd-theme');

    if (!themeSwitcher) {
        return;
    }

    const themeSwitcherText = document.querySelector('#bd-theme-text');
    const activeThemeIcon = document.querySelector('#theme-selector');
    const btnToActive = document.querySelector(`[data-bs-theme-value='${theme}']`);
    const classOfActiveBtn = (btnToActive.querySelector('svg') || btnToActive.querySelector('i')).classList;

    document.querySelectorAll('[data-bs-theme-value]').forEach(element => {
        element.classList.remove('active');
        element.setAttribute('aria-pressed', 'false');
    })

    btnToActive.classList.add('active');
    btnToActive.setAttribute('aria-pressed', 'true');
    if (activeThemeIcon.classList.length > 0) {
        const classes = filterIconClassList(activeThemeIcon.classList);
        activeThemeIcon.classList.remove(classes);
    }
    activeThemeIcon.classList.add(filterIconClassList(classOfActiveBtn));
    const themeSwitcherLabel = `${themeSwitcherText.textContent} (${btnToActive.dataset.bsThemeValue})`;
    themeSwitcher.setAttribute('aria-label', themeSwitcherLabel);

    if (focus) {
        themeSwitcher.focus();
    }
}

window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    const storedTheme = getStoredTheme();
    if (storedTheme !== 'light' && storedTheme !== 'dark') {
        setTheme(getPreferredTheme());
    }
});

function enableThemeSelectors() {
    document.querySelectorAll('[data-bs-theme-value]').forEach(toggle => {
        toggle.addEventListener('click', () => {
            const theme = toggle.getAttribute('data-bs-theme-value');
            setStoredTheme(theme);
            setTheme(theme);
            showActiveTheme(theme, true);
        });
    });
};

/** @type {(element: HTMLElement) => void} */
window.toggleDisplay = (element) => {
    if (element.classList.contains('d-none')) {
        element.classList.remove('d-none');
    } else {
        element.classList.add('d-none');
    }
};

/** @type {(element: HTMLElement) => void} */
window.selectTab = (element) => {
    const tabList = element.parentElement.parentElement;
    tabList.querySelectorAll('button.nav-link.active').forEach(btn => {
        btn.classList.remove('active');
    });
    element.classList.add('active');
};

/** @param {HTMLElement} target */
function initDropDowns(target) {
    if (target === null) return;
    for (const dropdownToggle of target.querySelectorAll('.nav-link.dropdown-toggle')) {
        new DropDown(dropdownToggle);
    }
}

/** @param {HTMLElement} target */
function initDataLists(target) {
    if (target === null) return;
    for (const dataList of target.getElementsByTagName('datalist')) {
        const input = dataList.parentElement.querySelector('.datalist-input');
        input?.setAttribute('list', dataList.id);
    }
}

function updateSelectedSection() {
    for (const navLink of document.querySelector('#mainNavBar').querySelectorAll('a.nav-link')) {
        navLink.classList.remove('active');
        if (navLink.href.endsWith("/")) {
            continue;
        }
        if (window.location.href.startsWith(navLink.href)) {
            navLink.classList.add('active');
        }
    }
}

window.addEventListener('htmx:afterSwap', (e) => {
    initDropDowns(e.detail?.target);
    // initDataLists(e.detail?.target);
});
window.addEventListener('htmx:load', () => {
    updateSelectedSection();
    enableThemeSelectors();
});
window.addEventListener('htmx:pushedIntoHistory', () => {
    updateSelectedSection();
    enableThemeSelectors();
});
window.addEventListener('DOMContentLoaded', () => {
    initDropDowns(document.body);
    updateSelectedSection();
    showActiveTheme(getPreferredTheme());
    enableThemeSelectors();
});
window.addEventListener('htmx:responseError', (e) => {
    /** @type {XMLHttpRequest | null} */
    const request = e.detail?.xhr;
    if (request === null) return;
    const event = new Event('createToast');
    event.detail = {
        message: request.responseText
            ? `${request.statusText} - ${request.responseText}`
            : request.statusText
    };
    document.dispatchEvent(event);
});

document.addEventListener('closeModal', (e) => {
    const id = e.detail?.id || '';
    /** @type {HTMLElement} */
    const target = e.target;
    if (!id && !target) {
        console.log('Could not find modal by id', id, e);
        return;
    }
    const element = document.getElementById(id) || target.closest('.modal');
    if (!element) {
        console.log('Could not find modal', e);
        return;
    }
    closeModal(element);
});

document.addEventListener('createToast', (e) => {
    const message = e.detail?.value;
    if (typeof message !== "string") {
        console.log('Could not create toast', e);
    }
    const toast = new Toast(message);
    toast.show();
});

document.addEventListener('refreshData', () => {
    for (const element of document.querySelectorAll("button[title='Refresh']")) {
        const event = new MouseEvent('click', {
            "view": window,
            "bubbles": false,
            "cancelable": false,
        });
        element.dispatchEvent(event);
    }
});

/** @type {(element: HTMLElement) => void} */
window.closeModal = (element) => {
    const container = document.getElementById('modals');
    const modal = element.classList.contains('modal') ? element : element.closest('.modal');
    if (!modal) {
        console.warn('Could not find modal to close');
        return;
    }
    const modalBackdrop = document.getElementById(`${modal.id}-backdrop`);

    modal.classList.remove(CLASS_NAME_SHOW);
    modalBackdrop.classList.remove(CLASS_NAME_SHOW);

    setTimeout(() => {
        container.removeChild(modal);
        container.removeChild(modalBackdrop);
    }, 200);
}

class DropDown {
    /** @param {HTMLElement} element */
    constructor(element) {
        /** @type {HTMLElement} */
        this.element = element;
        /** @type {HTMLUListElement} */
        this.menu = element.parentElement.querySelector('ul.dropdown-menu');
        this.element.addEventListener('click', (e) => {
            // e.preventDefault();
            this.toggle();
        });
        for (const item of this.menu.querySelectorAll('.dropdown-item')) {
            item.addEventListener('click', (e) => {
                // e.preventDefault();
                this.toggle();
            });
        }
    }

    toggle() {
        if (this.isShown) {
            this._hide();
        } else {
            this._show();
        }
    }

    get isShown() {
        return this.element.classList.contains(CLASS_NAME_SHOW);
    }

    _show() {
        this.element.classList.add(CLASS_NAME_SHOW);
        this.menu.classList.add(CLASS_NAME_SHOW);
        this.menu.setAttribute(ATTRIBUTE_NAME_POPPER, 'static');
    }

    _hide() {
        this.element.classList.remove(CLASS_NAME_SHOW);
        this.menu.classList.remove(CLASS_NAME_SHOW);
        this.menu.removeAttribute(ATTRIBUTE_NAME_POPPER);
    }
}

class Toast {
    /**
     * @param {string} body
     */
    constructor(body) {
        this.is_closed = false;
        this.container = document.getElementById('toasts');
        this.toast = document.createElement('div');
        this.toast.classList.add('toast', 'fade');
        this.toast.setAttribute('role', 'alert');
        this.toast.setAttribute('aria-live', 'assertive');
        this.toast.setAttribute('aria-atomic', 'true');
        const header = document.createElement('div');
        this.toast.appendChild(header);
        header.classList.add('toast-header');
        const img = document.createElement('img');
        header.appendChild(img);
        img.src = '/assets/bell_icon.png';
        img.width = 20;
        img.classList.add('me-1');
        const title = document.createElement('strong');
        header.appendChild(title);
        title.textContent = 'Workflow Engine';
        title.classList.add('me-auto');
        const dismiss = document.createElement('button');
        header.appendChild(dismiss);
        dismiss.type = 'button';
        dismiss.classList.add('btn-close');
        dismiss.setAttribute('aria-label', 'Close');
        dismiss.addEventListener('click', (ev) => {
            ev.preventDefault();
            this.close();
        });
        const content = document.createElement('div');
        this.toast.appendChild(content);
        content.classList.add('toast-body');
        content.textContent = body;
        this.container.appendChild(this.toast);
    }

    show() {
        this.toast.classList.add(CLASS_NAME_SHOW);
        setTimeout(() => this.close(), 5000);
    }

    close() {
        if (this.is_closed) {
            return;
        }
        this.is_closed = true;
        this.toast.classList.remove(CLASS_NAME_SHOW);
        this.container.removeChild(this.toast);
    }
}

/** @type {(button: HTMLButtonElement) => void} */
window.removeJobScheduleEntry = (button) => {
    const row = button.closest('.schedule-entry');
    row?.remove();
};

/** @type {(button: HTMLButtonElement) => void} */
window.removeWorkflowTask = (button) => {
    const row = button.closest('.workflow-task-item');
    row?.remove();
};
