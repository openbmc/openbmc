SUMMARY = "A collection of ASN.1-based protocols modules."
DESCRIPTION = "A collection of ASN.1 modules expressed in form of pyasn1\
 classes. Includes protocols PDUs definition (SNMP, LDAP etc.) and various\
 data structures (X.509, PKCS etc.)."
HOMEPAGE = "https://github.com/etingof/pyasn1-modules"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=190f79253908c986e6cacf380c3a5f6d"

SRC_URI[sha256sum] = "677091de870a80aae844b1ca6134f54652fa2c8c5a52aa396440ac3106e941e6"

PYPI_PACKAGE = "pyasn1_modules"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi ptest-python-pytest python_setuptools_build_meta

RDEPENDS:${PN} = "python3-pyasn1"

BBCLASSEXTEND = "native nativesdk"

