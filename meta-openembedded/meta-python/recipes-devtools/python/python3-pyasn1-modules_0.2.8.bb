SUMMARY = "A collection of ASN.1-based protocols modules."
DESCRIPTION = "A collection of ASN.1 modules expressed in form of pyasn1\
 classes. Includes protocols PDUs definition (SNMP, LDAP etc.) and various\
 data structures (X.509, PKCS etc.)."
HOMEPAGE = "https://github.com/etingof/pyasn1-modules"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a14482d15c2249de3b6f0e8a47e021fd"

SRC_URI[md5sum] = "107e1ece7d0a41d782f69f8a95a4d9bc"
SRC_URI[sha256sum] = "905f84c712230b2c592c19470d3ca8d552de726050d1d1716282a1f6146be65e"

inherit pypi ptest setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-pyasn1"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
