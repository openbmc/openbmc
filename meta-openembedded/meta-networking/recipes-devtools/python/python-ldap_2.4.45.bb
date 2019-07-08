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
DEPENDS = "python openldap cyrus-sasl"

PYPI_PACKAGE = "python-ldap"
inherit pypi setuptools

LIC_FILES_CHKSUM = "file://LICENCE;md5=36ce9d726d0321b73c1521704d07db1b"
SRC_URI[md5sum] = "6108e189a44eea8bc7d1cc281c222978"
SRC_URI[sha256sum] = "824fde180a53772e23edc031c4dd64ac1af4a3eade78f00d9d510937d562f64e"

do_configure_prepend() {
    sed -i -e 's:^library_dirs =.*::' setup.cfg
    sed -i -e 's:^include_dirs =.*:include_dirs = =/usr/include/sasl/:' setup.cfg
}

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
"
