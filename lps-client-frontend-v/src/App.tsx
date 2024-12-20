import React from 'react';
import { BodyLong, Box, Page } from "@navikt/ds-react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';
import logo from './logo.png'; // Adjust the path if necessary
import "@navikt/ds-css";

function App() {
    return (
        <Router >
            <Page >
                <Page.Block as="main" width="2xl" gutters>

                    <Box padding="4" style={{marginLeft: "auto", marginRight: "auto", maxWidth: "fit-content"}}>
                        <img src={logo} alt="Logo" style={{display: "block", maxWidth: '150px', marginLeft: "auto", marginRight: "auto"}}/>
                        <BodyLong>
                            Ditt lønns- og personalsystem
                        </BodyLong>
                    </Box>

                    <Routes>
                        <Route path="/" element={<LoginForm/>}/>
                        <Route path="/search" element={<FiltererInntektsmeldinger/>}/>
                        <Route path="/velkommen" element={<Velkommen/>}/>
                    </Routes>


                </Page.Block>
            </Page>

        </Router>
    );
}

export default App;
