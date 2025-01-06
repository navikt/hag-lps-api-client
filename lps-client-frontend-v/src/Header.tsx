import React from 'react';
import { Box, Heading, HStack } from "@navikt/ds-react";
import logo from "./logo.png"

function Header() {
    return (
        <Box padding="4" background="gray-100">
            <HStack gap="12" align="center">
                <Box style={{marginLeft: 72}}>
                    <img src={logo} alt="Logo"
                         style={{maxWidth: '150px'}}/>
                </Box>
                <Heading size="medium">
                TigerSys – Et helt greit lønns- og personalsystem
                </Heading>
            </HStack>
        </Box>
    );
}

export default Header;
