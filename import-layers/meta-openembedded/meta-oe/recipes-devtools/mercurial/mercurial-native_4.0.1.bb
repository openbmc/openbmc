SUMMARY = "The Mercurial distributed SCM"
HOMEPAGE = "http://mercurial.selenic.com/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "python-native"

SRC_URI = "https://www.mercurial-scm.org/release/${BP}.tar.gz \
           file://mercurial-CVE-2017-9462.patch \
"
SRC_URI[md5sum] = "22a9b1d7c0c06a53f0ae5b386d536d08"
SRC_URI[sha256sum] = "6aa4ade93c1b5e11937820880a466ebf1c824086d443cd799fc46e2617250d40"

S = "${WORKDIR}/mercurial-${PV}"

inherit native

EXTRA_OEMAKE = "STAGING_LIBDIR=${STAGING_LIBDIR} STAGING_INCDIR=${STAGING_INCDIR} \
    PREFIX=${prefix}"

do_configure_append () {
    sed -i -e 's:PYTHON=python:PYTHON=${STAGING_BINDIR_NATIVE}/python-native/python:g' ${S}/Makefile
}

do_install () {
    oe_runmake -e install-bin DESTDIR=${D} PREFIX=${prefix}
}

