import React, { useState } from 'react';
import { Alert, BodyShort, Box, Button, Heading, TextField, VStack } from "@navikt/ds-react";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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

            <VStack gap="4" justify="center">
                <Heading size="medium">
                    Logg inn for å ta i bruk systemet
                </Heading>
                {error && <Alert variant="error">{error}</Alert>}

                <TextField
                    label="Orgnr"
                    name="orgnr"
                    value={formData.orgnr}
                    onChange={handleInputChange}
                    maxLength={9}
                    style={{maxWidth: 110}}
                />


                <Button variant="primary" onClick={handleSubmit} style={{maxWidth: 340}}>
                    Logg inn
                </Button>

                <Button variant="secondary" onClick={handleRegistrerNyBedrift} style={{maxWidth: 340}}>
                    Registrer ny bedrift
                </Button>

            </VStack>

    );
}

export default LoginForm;
