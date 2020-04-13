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
SRC_URI[md5sum] = "fe22522208dc9b06d16eb70f8553eaab"
SRC_URI[sha256sum] = "7d1c4b15375a533564aad3d3deade789221e450052b21ebb9720fb822eccdb8e"

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
