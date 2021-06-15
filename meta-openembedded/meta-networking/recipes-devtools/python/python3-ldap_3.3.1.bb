#
# Copyright (C) 2012 Wind River Systems, Inc.
#
SUMMARY = "Provides a wrapper in Python to LDAP"
DESCRIPTION = "This module provides access to the LDAP \
(Lightweight Directory Access Protocol) through Python operations \
instead of C API. The module mainly acts as a wrapper for the \
OpenLDAP 2.x libraries. Errors will appear as exceptions."

LICENSE = "PSF"
HOMEPAGE = "http://www.python-ldap.org/"
DEPENDS = "python3 openldap cyrus-sasl"

PYPI_PACKAGE = "python-ldap"
inherit pypi setuptools3

LIC_FILES_CHKSUM = "file://LICENCE;md5=36ce9d726d0321b73c1521704d07db1b"
SRC_URI[md5sum] = "7608579722c491e42f5f63b3f88a95fb"
SRC_URI[sha256sum] = "4711cacf013e298754abd70058ccc995758177fb425f1c2d30e71adfc1d00aa5"

do_configure_prepend() {
    sed -i -e 's:^library_dirs =.*::' ${S}/setup.cfg
    sed -i -e 's:^include_dirs =.*:include_dirs = =/usr/include/sasl/:' ${S}/setup.cfg
}

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyasn1-modules \
"
