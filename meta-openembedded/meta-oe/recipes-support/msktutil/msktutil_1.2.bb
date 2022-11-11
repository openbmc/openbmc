SUMMARY = "Program for interoperability with Active Directory"
DESCRIPTION = "Msktutil creates user or computer accounts in Active Directory, \
               creates Kerberos keytabs on Unix/Linux systems, adds and removes \
               principals to and from keytabs and changes the user or computer \
               account's password."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "https://github.com/msktutil/msktutil/releases/download/v${PV}/msktutil-${PV}.tar.bz2"
SRC_URI[sha256sum] = "27dc078cbac3186540d8ea845fc0ced6b1d9f844e586ccd9eaa2d9f4650c2ce6"

DEPENDS += "krb5 cyrus-sasl openldap"

inherit autotools
