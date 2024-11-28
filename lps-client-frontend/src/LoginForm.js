import React, { useState } from 'react';
import { TextField, Button, Box, Alert } from '@mui/material';
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

    return (
        <Box sx={{ maxWidth: 400, mx: 'auto', mt: 4 }}>
            {error && <Alert severity="error">{error}</Alert>}
            <Box component="form" noValidate autoComplete="true">
                <TextField
                    label="Kid"
                    name="kid"
                    value={formData.kid}
                    onChange={handleInputChange}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Private key"
                    name="privateKey"
                    value={formData.privateKey}
                    onChange={handleInputChange}
                    multiline
                    rows={4}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Issuer"
                    name="issuer"
                    value={formData.issuer}
                    onChange={handleInputChange}
                    fullWidth
                    margin="normal"
                />
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
        </Box>
    );
}

export default LoginForm;