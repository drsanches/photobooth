#!/bin/sh

ROOT_DIR="ca"
CERT_DIR="ssl"
ROOT_KEY_NAME="$ROOT_DIR/rootCA.key"
ROOT_CRT_NAME="$ROOT_DIR/rootCA.crt"
KEY_NAME="$CERT_DIR/domain.key"
CSR_NAME="$CERT_DIR/domain.csr"
CRT_NAME="$CERT_DIR/domain.crt"
EXT_NAME="$CERT_DIR/domain.ext"

YELLOW='\033[1;33m'
NO_COLOR='\033[0m'

# Check argument
if [ ! "$1" ]; then printf "ERROR: Send domain as script argument\n"; exit 1; fi

# Create directories
if [ ! -d "$ROOT_DIR" ]; then mkdir "$ROOT_DIR"; fi
if [ ! -d "$CERT_DIR" ]; then mkdir "$CERT_DIR"; fi

# Create a self-signed root CA
# WITHOUT PASSWORD!!! Remove -nodes to use password
printf "${YELLOW}\n=== GENERATING ROOT CA ===\n\n${NO_COLOR}";
openssl req -nodes -x509 -sha256 -days 1825 -newkey rsa:2048 -keyout "$ROOT_KEY_NAME" -out "$ROOT_CRT_NAME";

# Create both the private key and certificate signing request
# WITHOUT PASSWORD!!! Remove -nodes to use password
printf "${YELLOW}\n=== GENERATING DOMAIN CERT ===\n\n${NO_COLOR}";
openssl req -nodes -newkey rsa:2048 -keyout "$KEY_NAME" -out "$CSR_NAME";

echo "authorityKeyIdentifier=keyid,issuer" > $EXT_NAME;
echo "basicConstraints=CA:FALSE" >> $EXT_NAME;
echo "subjectAltName = @alt_names" >> $EXT_NAME;
echo "[alt_names]" >> $EXT_NAME;
echo "DNS.1 = $1" >> $EXT_NAME;

# Sign csr with the root CA certificate and its private key
openssl x509 -req -CAcreateserial -days 365 -extfile $EXT_NAME -CA $ROOT_CRT_NAME -CAkey $ROOT_KEY_NAME -in $CSR_NAME -out $CRT_NAME;
