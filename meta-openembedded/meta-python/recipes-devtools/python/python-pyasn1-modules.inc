SUMMARY = "A collection of ASN.1-based protocols modules."
DESCRIPTION = "A collection of ASN.1 modules expressed in form of pyasn1\
 classes. Includes protocols PDUs definition (SNMP, LDAP etc.) and various\
 data structures (X.509, PKCS etc.)."
HOMEPAGE = "https://github.com/etingof/pyasn1-modules"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=815ac3f9ca693006349094b358ab3155"

SRC_URI[md5sum] = "65ce1ea1021679bffe96065baab232de"
SRC_URI[sha256sum] = "a0cf3e1842e7c60fde97cb22d275eb6f9524f5c5250489e292529de841417547"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-pyasn1"

BBCLASSEXTEND = "native nativesdk"
