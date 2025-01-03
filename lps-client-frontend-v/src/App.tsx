import React from 'react';
import { Page, VStack } from "@navikt/ds-react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';
import "@navikt/ds-css";
import Header from './Header';

function App() {
    return (
        <Router>
            <Header/>
            <Page>
                <Page.Block as="main" width="2xl" gutters>
                    <VStack gap="10" margin="10" align="stretch" justify="center">
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
