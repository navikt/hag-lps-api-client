import React from 'react';
import ReactDOM from 'react-dom/client';
import { CssBaseline, Container, Typography } from '@mui/material';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';

function App() {
  return (
    <Router>
      <Container>
        <CssBaseline />
        <Typography variant="h4" align="center" gutterBottom>
          TigerSys
        </Typography>
        <Typography variant="subtitle1" align="center" gutterBottom>
          Ditt lønns- og personalsystem
        </Typography>
        <Routes>
          <Route path="/" element={<LoginForm />} />
          <Route path="/search" element={<FiltererInntektsmeldinger />} />
          <Route path="/velkommen" element={<Velkommen />} />
        </Routes>
      </Container>
    </Router>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);

export default App;
