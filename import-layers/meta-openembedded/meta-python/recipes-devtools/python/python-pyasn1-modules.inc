SUMMARY = "A collection of ASN.1-based protocols modules."
DESCRIPTION = "A collection of ASN.1 modules expressed in form of pyasn1\
 classes. Includes protocols PDUs definition (SNMP, LDAP etc.) and various\
 data structures (X.509, PKCS etc.)."
HOMEPAGE = "https://github.com/etingof/pyasn1-modules"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=280606d9c18f200e03e0c247ac61475a"

SRC_URI[md5sum] = "ff938698590f314ff9b3d5c48737d899"
SRC_URI[sha256sum] = "af00ea8f2022b6287dc375b2c70f31ab5af83989fc6fe9eacd4976ce26cd7ccc"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-pyasn1"

BBCLASSEXTEND = "native nativesdk"
