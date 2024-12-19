import React from 'react';
import ReactDOM from 'react-dom/client';
import {CssBaseline, Container, Typography, Box} from '@mui/material';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';
import logo from './logo.png'; // Adjust the path if necessary

function App() {
    return (
        <Router>
            <Container>
                <CssBaseline/>
                <Box display="flex" justifyContent="center" mt={4}>
                    <img src={logo} alt="Logo" style={{maxWidth: '150px'}}/>
                </Box>

                <Typography variant="subtitle1" align="center" gutterBottom>
                    Ditt lønns- og personalsystem
                </Typography>
                <Routes>
                    <Route path="/" element={<LoginForm/>}/>
                    <Route path="/search" element={<FiltererInntektsmeldinger/>}/>
                    <Route path="/velkommen" element={<Velkommen/>}/>
                </Routes>
            </Container>
        </Router>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App/>);

export default App;
