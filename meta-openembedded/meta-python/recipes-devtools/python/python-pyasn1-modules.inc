SUMMARY = "A collection of ASN.1-based protocols modules."
DESCRIPTION = "A collection of ASN.1 modules expressed in form of pyasn1\
 classes. Includes protocols PDUs definition (SNMP, LDAP etc.) and various\
 data structures (X.509, PKCS etc.)."
HOMEPAGE = "https://github.com/etingof/pyasn1-modules"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a14482d15c2249de3b6f0e8a47e021fd"

SRC_URI[md5sum] = "c0bbe9820c971a97f156ff343ae67911"
SRC_URI[sha256sum] = "43c17a83c155229839cc5c6b868e8d0c6041dba149789b6d6e28801c64821722"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-pyasn1"

BBCLASSEXTEND = "native nativesdk"
