import React, {useState} from 'react';
import {Alert, Box, Button, TextField} from '@mui/material';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';

function LoginForm() {
    const [formData, setFormData] = useState({orgnr: ''});
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async () => {
        setError(null);
        try {
            const response = await axios.post(`${process.env.REACT_APP_BASE_URL}/systembruker`, new URLSearchParams(formData), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });

            localStorage.setItem('token', response.data.tokenResponse.access_token);
            localStorage.setItem('exp', Date.now() + response.data.tokenResponse.expires_in * 1000);
            localStorage.setItem('orgnr', formData.orgnr);

            navigate('/search');
        } catch (error) {
            console.log('Error:', error);
            setError(error.response.data);
        }
    };

    const handleRegistrerNyBedrift = async () => {
        setError(null);
        try {
            const response = await axios.post(`${process.env.REACT_APP_BASE_URL}/registrer-ny-bedrift`, {
                kundeOrgnr: formData.orgnr,
            }, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });
            if (!!response.data.confirmUrl) {
                window.open(response.data.confirmUrl, '_blank');

            } else {
                throw Error("Klarte ikke hente bekreftelses-url fra registreringsrespons.")
            }
        } catch (error) {
            console.log('Error:', error);
            setError('Noe gikk galt da vi skulle registrere din bedrift som ny kunde. Det kan skyldes at den allerede er registrert.' + error.message);
        }
    };

    return (
        <Box sx={{maxWidth: 400, mx: 'auto', mt: 4}}>
            {error && <Alert severity="error">{error}</Alert>}
            <Box component="form" noValidate autoComplete="true">
                <TextField
                    label="Consumer orgnr"
                    name="orgnr"
                    value={formData.orgnr}
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
