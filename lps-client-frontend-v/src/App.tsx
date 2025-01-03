import React from 'react';
import { Box, Heading, HStack, Page, VStack } from "@navikt/ds-react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginForm from './LoginForm';
import FiltererInntektsmeldinger from './FiltererInntektsmeldinger';
import Velkommen from './Velkommen';
import logo from './logo.png'; // Adjust the path if necessary
import "@navikt/ds-css";

function App() {
    return (
        <Router>
            <Box padding="4" background="gray-100">
                <HStack gap="12" align="center">
                    <img src={logo} alt="Logo"
                         style={{maxWidth: '150px'}}/>
                    <Heading size="medium">
                        TigerSys lønns- og personalsystem
                    </Heading>

                </HStack>
            </Box>
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
