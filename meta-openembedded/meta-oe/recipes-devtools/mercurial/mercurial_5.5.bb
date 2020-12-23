SUMMARY = "The Mercurial distributed SCM"
HOMEPAGE = "http://mercurial.selenic.com/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "python3 python3-native"
RDEPENDS_${PN} = "python3 python3-modules"

inherit python3native

SRC_URI = "https://www.mercurial-scm.org/release/${BP}.tar.gz"
SRC_URI[md5sum] = "17b21729cbc61dda80b2e3dfc046319f"
SRC_URI[sha256sum] = "c1ed28e1534304a7a4981ed59905286d1c56acd5b75755eedd184171a4a782b4"

S = "${WORKDIR}/mercurial-${PV}"

BBCLASSEXTEND = "native"

export LDSHARED="${CCLD} -shared"

EXTRA_OEMAKE = "STAGING_LIBDIR=${STAGING_LIBDIR} STAGING_INCDIR=${STAGING_INCDIR} \
    PREFIX=${prefix}"

do_configure_append () {
    sed -i -e 's:PYTHON?=python:PYTHON=python3:g' ${S}/Makefile
}

do_install () {
    oe_runmake -e install-bin DESTDIR=${D} PREFIX=${prefix}
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/python3-native/python3:${USRBINPATH}/env python3:g' ${D}${bindir}/hg
}
PACKAGES =+ "${PN}-python"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"

FILES_${PN}-python = "${nonarch_libdir}/${PYTHON_DIR}"

