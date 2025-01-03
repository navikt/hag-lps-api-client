import React from 'react';
import { BodyLong, Page, VStack } from "@navikt/ds-react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';
import logo from './logo.png'; // Adjust the path if necessary
import "@navikt/ds-css";

function App() {
    return (
        <Router>
            <Page>
                <Page.Block as="main" width="2xl" gutters>
                    <VStack gap="10" margin="10">
                        <img src={logo} alt="Logo"
                             style={{maxWidth: '400px', marginLeft: "auto", marginRight: "auto"}}/>
                        <BodyLong size="large" style={{marginLeft: "auto", marginRight: "auto"}}>
                            Ditt lønns- og personalsystem
                        </BodyLong>

                        <Routes>
                            <Route path="/" element={<LoginForm/>}/>
                            <Route path="/search" element={<FiltererInntektsmeldinger/>}/>
                            <Route path="/velkommen" element={<Velkommen/>}/>
                        </Routes>
                    </VStack>
                </Page.Block>
            </Page>
        </Router>
    );
}

export default App;
