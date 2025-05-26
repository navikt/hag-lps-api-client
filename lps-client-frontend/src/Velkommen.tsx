import React from 'react';
import { BodyShort, Box, Button, Heading, VStack } from "@navikt/ds-react";
import { useNavigate } from 'react-router-dom';

function Velkommen() {
    const navigate = useNavigate();

    const handleGaaTilForsiden = async () => {
        navigate('/');
    };

    return (
        <VStack gap="5" justify="center">
            <Box>
                <Heading size="small" spacing>
                    Velkommen!
                </Heading>

                <BodyShort spacing>
                    Din bedrift er nå registrert som kunde i vårt lønns- og personalsystem 🎉
                </BodyShort>

                <BodyShort spacing>
                    Gå til forsiden og logg inn for å starte din sykepengereise.
                </BodyShort>
            </Box>

            <Button onClick={handleGaaTilForsiden} style={{maxWidth: 340}}>
                Gå til forsiden
            </Button>
        </VStack>
    );
}

export default Velkommen;
