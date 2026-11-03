(function () {
    const apiBase = `${window.location.origin}/api/v1`;

    async function apiRequest(path, options = {}) {
        const { auth = true, ...requestOptions } = options;
        const headers = new Headers(requestOptions.headers || {});
        const token = auth ? localStorage.getItem('token') : null;

        if (token) headers.set('Authorization', `Bearer ${token}`);
        if (requestOptions.body && !(requestOptions.body instanceof FormData) && !headers.has('Content-Type')) {
            headers.set('Content-Type', 'application/json');
        }

        const response = await fetch(`${apiBase}${path}`, { ...requestOptions, headers });
        const responseText = await response.text();
        const isJson = response.headers.get('content-type')?.includes('application/json');
        const responseBody = isJson && responseText ? JSON.parse(responseText) : responseText;

        if (!response.ok) {
            if (response.status === 401 && window.location.pathname !== '/login.html') {
                localStorage.removeItem('token');
                localStorage.removeItem('usuarioLogado');
                window.location.href = '/login.html';
            }
            const errorMessage = typeof responseBody === 'object' && responseBody !== null
                ? responseBody.message || responseBody.detail || responseBody.error
                : responseBody;
            const error = new Error(errorMessage || `A API respondeu com status ${response.status}.`);
            error.status = response.status;
            throw error;
        }

        if (!responseText) return null;
        return responseBody;
    }

    window.apiRequest = apiRequest;
})();
