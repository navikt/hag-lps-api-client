import React, { useState } from 'react';
import { TextField, Button, Box, Alert, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { DateTimePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { checkAndRefreshToken } from './utils';

function FiltererInntektsmeldinger() {
    const [secondFormData, setSecondFormData] = useState({ forespoerselid: '', fnr: '', datoFra: null, datoTil: null });
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
            [name]: date ? dayjs(date).toISOString().slice(0, 19) : null,
        });
    };

    const handleSecondFormSubmit = async () => {
        try {
            setError(null);
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
                                label="Forespørsel Id"
                                name="foresporselid"
                                value={secondFormData.foresporselid}
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
                            <DateTimePicker
                                label="Dato Fra"
                                value={secondFormData.datoFra ? dayjs(secondFormData.datoFra) : null}
                                onChange={(date) => handleDateChange('datoFra', date)}
                                renderInput={(params) => <TextField {...params} fullWidth />}
                            />
                        </Box>
                        <Box flex={1} minWidth={200}>
                            <DateTimePicker
                                label="Dato Til"
                                value={secondFormData.datoTil ? dayjs(secondFormData.datoTil) : null}
                                onChange={(date) => handleDateChange('datoTil', date)}
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
                                    <TableCell>Forespørsel Id</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Sykemeldt</TableCell>
                                    <TableCell>Organisasjonsnr</TableCell>
                                    <TableCell>Beløp</TableCell>
                                    <TableCell>Sykmeldingsperioder</TableCell>
                                    <TableCell>Innsendt</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {results.inntektsmeldinger.map((inntektsmelding) => (
                                    <TableRow key={inntektsmelding.dokument.id}>
                                        <TableCell>{inntektsmelding.dokument.id}</TableCell>
                                        <TableCell>{inntektsmelding.dokument.type.type}</TableCell>
                                        <TableCell>{inntektsmelding.dokument.sykmeldt.fnr + ' - ' + inntektsmelding.dokument.sykmeldt.navn}</TableCell>
                                        <TableCell>{inntektsmelding.dokument.avsender.orgnr}</TableCell>
                                        <TableCell>{inntektsmelding.dokument.inntekt.beloep}</TableCell>
                                        <TableCell>{inntektsmelding.dokument.sykmeldingsperioder.map((sykmeldingsperiode) => (sykmeldingsperiode.fom + ' - ' + sykmeldingsperiode.tom)).join(', ')}</TableCell>
                                        <TableCell>{new Date(inntektsmelding.dokument.mottatt).toLocaleDateString('nb-NO')}</TableCell>
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
