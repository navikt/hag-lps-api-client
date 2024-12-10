import React, { useState } from 'react';
import { Alert, Box, Button, TextField } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function LoginForm() {
    const [formData, setFormData] = useState({ kid: '', privateKey: '', issuer: '', consumerOrgNr: '' });
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async () => {
        try {
            const response = await axios.post('https://hag-lps-api-client.ekstern.dev.nav.no/getToken', new URLSearchParams(formData), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });
            localStorage.setItem('token', response.data.tokenResponse.access_token);
            localStorage.setItem('exp', Date.now() + response.data.tokenResponse.expires_in * 1000);
            localStorage.setItem('kid', formData.kid);
            localStorage.setItem('privateKey', formData.privateKey);
            localStorage.setItem('issuer', formData.issuer);
            localStorage.setItem('consumerOrgNr', formData.consumerOrgNr);
            navigate('/search');
        } catch (error) {
            setError('Error submitting form');
        }
    };

    const handleRegistrerNyBedrift = async () => {
        try {
            const response = await axios.post('http://localhost:8080/registrer-ny-bedrift', {
                kundeOrgnr: formData.consumerOrgNr,
            }, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });
            if (!!response.data.confirmUrl) {
                window.location.href = response.data.confirmUrl;
            } else {
                throw Error("Klarte ikke hente bekreftelses-url fra registreringsrespons.")
            }
        } catch (error) {
            setError('Noe gikk galt da vi skulle registrere din bedrift som ny kunde. Det kan skyldes at den allerede er registrert.');
        }
    };

    return (
        <Box sx={{ maxWidth: 400, mx: 'auto', mt: 4 }}>
            {error && <Alert severity="error">{error}</Alert>}
            <Box component="form" noValidate autoComplete="true">
                <TextField
                    label="Consumer orgnr"
                    name="consumerOrgNr"
                    value={formData.consumerOrgNr}
                    onChange={handleInputChange}
                    fullWidth
                    margin="normal"
                />
                <Button variant="contained" color="primary" onClick={handleSubmit} fullWidth>
                    Login
                </Button>
            </Box>
            <Box mt={2}>
                <Button variant="contained" color="secondary" onClick={handleRegistrerNyBedrift} fullWidth>
                    Registrer ny bedrift
                </Button>
            </Box>
        </Box>
    );
}

export default LoginForm;
