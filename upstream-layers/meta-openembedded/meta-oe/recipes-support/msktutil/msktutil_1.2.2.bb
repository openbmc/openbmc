SUMMARY = "Program for interoperability with Active Directory"
DESCRIPTION = "Msktutil creates user or computer accounts in Active Directory, \
               creates Kerberos keytabs on Unix/Linux systems, adds and removes \
               principals to and from keytabs and changes the user or computer \
               account's password."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "https://github.com/msktutil/msktutil/releases/download/${PV}/${BP}.tar.bz2 \
           file://0001-configure.ac-Remove-native-include-path.patch"
SRC_URI[sha256sum] = "51314bb222c20e963da61724c752e418261a7bfc2408e7b7d619e82a425f6541"

UPSTREAM_CHECK_URI = "https://github.com/msktutil/msktutil/releases/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

DEPENDS += "krb5 cyrus-sasl openldap"

inherit autotools
