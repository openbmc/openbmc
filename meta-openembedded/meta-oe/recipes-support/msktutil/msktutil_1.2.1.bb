SUMMARY = "Program for interoperability with Active Directory"
DESCRIPTION = "Msktutil creates user or computer accounts in Active Directory, \
               creates Kerberos keytabs on Unix/Linux systems, adds and removes \
               principals to and from keytabs and changes the user or computer \
               account's password."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "https://github.com/msktutil/msktutil/releases/download/${PV}/${BP}.tar.bz2 \
           file://0001-configure.ac-Remove-native-include-path.patch"
SRC_URI[sha256sum] = "f9686237c4e24414802415f4c8627c7343da365c5a3bcdef7a853fa3cd27b45d"

DEPENDS += "krb5 cyrus-sasl openldap"

inherit autotools
