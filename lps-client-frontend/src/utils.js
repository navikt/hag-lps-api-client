import axios from 'axios';

export const checkAndRefreshToken = async () => {
    const token = localStorage.getItem('token');
    const exp = localStorage.getItem('exp');
    console.log("sjekker for refresh token");
    if (!token || !exp || Date.now() >= exp) {
        const formData = {
            orgnr: localStorage.getItem('orgnr')
        };
        console.log("henter nytt token");
        const response = await axios.post(`${import.meta.env.VITE_REACT_APP_BASE_URL}/systembruker`, new URLSearchParams(formData), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        localStorage.setItem('token', response.data.tokenResponse.access_token);
        localStorage.setItem('exp', Date.now() + response.data.tokenResponse.expires_in * 1000);
    }
};
