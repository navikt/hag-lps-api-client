import React, {useState} from 'react';
import { Button, Box, BodyLong, VStack, TextField, Alert} from "@navikt/ds-react";
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

            const response = await axios.post(`${import.meta.env.VITE_REACT_APP_BASE_URL}/systembruker`, new URLSearchParams(formData), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });

            localStorage.setItem('token', response.data.tokenResponse.access_token);
            localStorage.setItem('exp', Date.now() + response.data.tokenResponse.expires_in * 1000);
            localStorage.setItem('orgnr', formData.orgnr);

            navigate('/search');
        } catch (error) {
            setError(error?.response?.data || "Noe gikk galt.");
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
            setError('Noe gikk galt da vi skulle registrere din bedrift som ny kunde. Det kan skyldes at den allerede er registrert.' + error.message);
        }
    };

    return (
        <Box >
            {error && <Alert variant="error">{error}</Alert>}
            <Box >
                <TextField
                    label="Consumer orgnr"
                    name="orgnr"
                    value={formData.orgnr}
                    onChange={handleInputChange}
                />
                <Button variant="primary" onClick={handleSubmit} >
                    Logg inn
                </Button>
            </Box>
            <Box >
                <Button variant="secondary" onClick={handleRegistrerNyBedrift} >
                    Registrer ny bedrift
                </Button>
            </Box>
        </Box>
    );
}

export default LoginForm;
