SUMMARY = "Provides a wrapper in Python to LDAP"
DESCRIPTION = "This module provides access to the LDAP \
(Lightweight Directory Access Protocol) through Python operations \
instead of C API. The module mainly acts as a wrapper for the \
OpenLDAP 2.x libraries. Errors will appear as exceptions."
HOMEPAGE = "https://www.python-ldap.org/"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=36ce9d726d0321b73c1521704d07db1b"

DEPENDS = "python3 openldap cyrus-sasl python3-setuptools-scm-native"

PYPI_PACKAGE = "python_ldap"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "b2f6ef1c37fe2c6a5a85212efe71311ee21847766a7d45fcb711f3b270a5f79a"

do_configure:prepend() {
    sed -i -e 's:^library_dirs =.*::' \
        -e 's:^include_dirs =.*:include_dirs = =/usr/include/sasl/:' \
        -e 's/= ldap_r/= ldap/g' ${S}/setup.cfg
}

RDEPENDS:${PN} = " \
    python3-pprint \
    python3-pyasn1 \
    python3-pyasn1-modules \
    python3-threading \
    python3-unittest \
"

CVE_PRODUCT = "python-ldap"
