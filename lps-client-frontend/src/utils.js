import axios from 'axios';

export const checkAndRefreshToken = async () => {
    const token = localStorage.getItem('token');
    const exp = localStorage.getItem('exp');

    if (!token || !exp || Date.now() >= exp) {
        const formData = {
            kid: localStorage.getItem('kid'),
            privateKey: localStorage.getItem('privateKey'),
            issuer: localStorage.getItem('issuer'),
            consumerOrgNr: localStorage.getItem('consumerOrgNr')
        };

        const response = await axios.post('https://hag-lps-api-client.ekstern.dev.nav.no/getToken', new URLSearchParams(formData), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        localStorage.setItem('token', response.data.tokenResponse.access_token);
        localStorage.setItem('exp', Date.now() + response.data.tokenResponse.expires_in * 1000);
    }
};