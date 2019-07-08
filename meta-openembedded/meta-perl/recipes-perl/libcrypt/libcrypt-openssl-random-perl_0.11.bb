SUMMARY = "Crypt Openssl Random cpan module"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=089c18d635ae273e1727ec385e64063b"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-Random-${PV}.tar.gz "
SRC_URI[md5sum] = "5d71337503e0356ce1ce1481504e5885"
SRC_URI[sha256sum] = "bb8c81c6a39b9b13a22d818ee9a746242f136f0fadceb6b9776ae615e7524c7a"

S = "${WORKDIR}/Crypt-OpenSSL-Random-${PV}"

DEPENDS += " openssl \
"
inherit cpan
