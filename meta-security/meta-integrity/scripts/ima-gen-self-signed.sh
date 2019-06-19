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

GENKEY=ima.genkey

cat << __EOF__ >$GENKEY
[ req ]
default_bits = 1024
distinguished_name = req_distinguished_name
prompt = no
string_mask = utf8only
x509_extensions = myexts

[ req_distinguished_name ]
O = example.com
CN = meta-intel-iot-security example signing key
emailAddress = john.doe@example.com

[ myexts ]
basicConstraints=critical,CA:FALSE
keyUsage=digitalSignature
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid
__EOF__

openssl req -new -nodes -utf8 -sha1 -days 36500 -batch \
        -x509 -config $GENKEY \
	-outform DER -out x509_ima.der -keyout privkey_ima.pem
