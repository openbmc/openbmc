SUMMARY = "Fast and portable XML parser and Jabber protocol library"
HOMEPAGE = "https://github.com/meduketto/iksemel"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRCREV = "978b733462e41efd5db72bc9974cb3b0d1d5f6fa"
PV = "1.5+git"

SRC_URI = "git://github.com/meduketto/iksemel.git;protocol=https;branch=master \
           file://fix-configure-option-parsing.patch \
           file://avoid-obsolete-gnutls-apis.patch"

# The current PV is not a git tag but a README content
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig lib_package texinfo

# TLS support requires either openssl or gnutls (if both are enabled openssl will be used).
PACKAGECONFIG ?= "gnutls"

PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"

EXTRA_OECONF = "--disable-python"
