import React from 'react';
import {Box, Button, Typography} from '@mui/material';
import {useNavigate} from 'react-router-dom';

function Velkommen() {
    const navigate = useNavigate();

    const handleGaaTilForsiden = async () => {
        navigate('/');
    };

    return (
        <Box sx={{maxWidth: 400, mx: 'auto', mt: 4, gap: 20}}>
            <Box mt={2}>
                <Typography variant="h4" align="center" gutterBottom>
                    Velkommen!
                </Typography>

                <Typography variant="h6" align="center" gutterBottom>
                    Din bedrift er nå registrert som kunde i vårt lønns- og personalsystem 🎉
                </Typography>

                <Typography variant="h6" align="center" gutterBottom>
                    Gå til forsiden og logg inn for å starte ditt inntektsmeldingseventyr.
                </Typography>
            </Box>
            <Box mt={2}>
                <Button variant="contained" color="primary" onClick={handleGaaTilForsiden} fullWidth>
                    Gå til forsiden
                </Button>
            </Box>
        </Box>
    );
}

export default Velkommen;
