#!/bin/sh
#
# Copied from ima-evm-utils.
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# version 2 as published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

GENKEY=ima-local-ca.genkey

cat << __EOF__ >$GENKEY
[ req ]
default_bits = 2048
distinguished_name = req_distinguished_name
prompt = no
string_mask = utf8only
x509_extensions = v3_ca

[ req_distinguished_name ]
O = example.com
CN = meta-intel-iot-security example certificate signing key
emailAddress = john.doe@example.com

[ v3_ca ]
basicConstraints=CA:TRUE
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
# keyUsage = cRLSign, keyCertSign
__EOF__

openssl req -new -x509 -utf8 -sha1 -days 3650 -batch -config $GENKEY \
        -outform DER -out ima-local-ca.x509 -keyout ima-local-ca.priv

openssl x509 -inform DER -in ima-local-ca.x509 -out ima-local-ca.pem
