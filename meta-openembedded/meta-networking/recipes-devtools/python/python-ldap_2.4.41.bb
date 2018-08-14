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

LIC_FILES_CHKSUM = "file://LICENCE;md5=a41c82edffa04912007cae1d20cac555"
SRC_URI[md5sum] = "18db2d009150ec1864710fea3ed76173"
SRC_URI[sha256sum] = "6d430ecf040f2fc704ee316d3390cb1f5419c191371e1e131baef54a0e42cef0"

do_configure_prepend() {
    sed -i -e 's:^library_dirs =.*::' setup.cfg
    sed -i -e 's:^include_dirs =.*:include_dirs = =/usr/include/sasl/:' setup.cfg
}

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-threading \
"
