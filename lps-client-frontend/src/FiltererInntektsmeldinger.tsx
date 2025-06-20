import React, { useState } from 'react';
import { TextField, Button, Box, Alert, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { checkAndRefreshToken } from './utils';

function FiltererInntektsmeldinger() {
    const [secondFormData, setSecondFormData] = useState({ navReferanseId: '', fnr: '', fom: null, tom: null });
    const [error, setError] = useState(null);
    const [results, setResults] = useState(null);
    const navigate = useNavigate();

    const handleSecondFormChange = (e) => {
        setSecondFormData({
            ...secondFormData,
            [e.target.name]: e.target.value,
        });
    };

    const handleDateChange = (name, date) => {
        setSecondFormData({
            ...secondFormData,
            [name]: date,
        });
    };

    const handleSecondFormSubmit = async () => {
        try {
            setError(null);
            setResults(null);
            await checkAndRefreshToken();
            const token = localStorage.getItem('token');
            const response = await axios.post(`${import.meta.env.VITE_REACT_APP_BASE_URL}/filterInntektsmeldingerToken`, secondFormData, {
                headers: {
                    "authorization": `${token}`,
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
            });
            setResults(response.data);
        } catch (error) {
            setError(error.response.data);
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate('/');
    };

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <Box sx={{ width: '100%', mt: 4 }}>
                {error && <Alert severity="error">{error}</Alert>}
                <Box component="form" noValidate autoComplete="off" sx={{ mb: 4 }}>
                    <Box display="flex" flexWrap="wrap" gap={2}>
                        <Box flex={1} minWidth={200}>
                            <TextField
                                label="NavReferanseId"
                                name="navReferanseId"
                                value={secondFormData.navReferanseId}
                                onChange={handleSecondFormChange}
                                fullWidth
                            />
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <TextField
                                label="Fnr"
                                name="fnr"
                                value={secondFormData.fnr}
                                onChange={handleSecondFormChange}
                                fullWidth
                            />
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <DatePicker
                                label="Dato Fra"
                                name="fom"
                                format="YYYY-MM-DD"
                                value={secondFormData.fom}
                                onChange={(date) => handleDateChange('fom', date)}
                                renderInput={(params) => <TextField {...params} fullWidth />}
                            />
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <DatePicker
                                label="Dato Til"
                                name="tom"
                                format="YYYY-MM-DD"
                                value={secondFormData.tom}
                                onChange={(date) => handleDateChange('tom', date)}
                                renderInput={(params) => <TextField {...params} fullWidth />}
                            />
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <Button variant="contained" color="primary" onClick={handleSecondFormSubmit} fullWidth>
                                Søk
                            </Button>
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <Button variant="contained" color="secondary" onClick={handleLogout} fullWidth>
                                Logg ut
                            </Button>
                        </Box>
                    </Box>
                </Box>
                {results && (
                    <TableContainer component={Paper} sx={{ width: '100%', overflowX: 'auto' }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>NavReferanseId</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Sykemeldt</TableCell>
                                    <TableCell>Organisasjonsnr</TableCell>
                                    <TableCell>Beløp</TableCell>
                                    <TableCell>Innsendt</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {results.map((inntektsmelding) => (
                                    <TableRow key={inntektsmelding.navReferanseId}>
                                        <TableCell>{inntektsmelding.navReferanseId}</TableCell>
                                        <TableCell>{inntektsmelding.typeInnsending}</TableCell>
                                        <TableCell>{inntektsmelding.sykmeldtFnr}</TableCell>
                                        <TableCell>{inntektsmelding.arbeidsgiver.orgnr}</TableCell>
                                        <TableCell>{inntektsmelding.inntekt.beloep}</TableCell>
                                        <TableCell>{new Date(inntektsmelding.innsendtTid).toLocaleDateString('nb-NO')}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Box>
        </LocalizationProvider>
    );
}

export default FiltererInntektsmeldinger;
