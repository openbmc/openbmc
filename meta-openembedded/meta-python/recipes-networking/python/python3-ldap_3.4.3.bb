SUMMARY = "Provides a wrapper in Python to LDAP"
DESCRIPTION = "This module provides access to the LDAP \
(Lightweight Directory Access Protocol) through Python operations \
instead of C API. The module mainly acts as a wrapper for the \
OpenLDAP 2.x libraries. Errors will appear as exceptions."
HOMEPAGE = "http://www.python-ldap.org/"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=36ce9d726d0321b73c1521704d07db1b"

DEPENDS = "python3 openldap cyrus-sasl"

PYPI_PACKAGE = "python-ldap"

inherit pypi setuptools3

SRC_URI[sha256sum] = "ab26c519a0ef2a443a2a10391fa3c5cb52d7871323399db949ebfaa9f25ee2a0"

do_configure:prepend() {
    sed -i -e 's:^library_dirs =.*::' \
        -e 's:^include_dirs =.*:include_dirs = =/usr/include/sasl/:' \
        -e 's/= ldap_r/= ldap/g' ${S}/setup.cfg
}

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyasn1-modules \
"
