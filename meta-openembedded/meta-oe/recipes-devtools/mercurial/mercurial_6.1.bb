SUMMARY = "The Mercurial distributed SCM"
HOMEPAGE = "http://mercurial.selenic.com/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "python3 python3-native"
RDEPENDS:${PN} = "python3 python3-modules"

inherit python3native

SRC_URI = "https://www.mercurial-scm.org/release/${BP}.tar.gz"
SRC_URI[sha256sum] = "86f98645e4565a9256991dcde22b77b8e7d22ca6fbb60c1f4cdbd8469a38cc1f"

S = "${WORKDIR}/mercurial-${PV}"

BBCLASSEXTEND = "native"

export LDSHARED="${CCLD} -shared"

EXTRA_OEMAKE = "STAGING_LIBDIR=${STAGING_LIBDIR} STAGING_INCDIR=${STAGING_INCDIR} \
    PREFIX=${prefix}"

do_configure:append () {
    sed -i -e 's:PYTHON?=python:PYTHON=python3:g' ${S}/Makefile
}

do_install () {
    oe_runmake -e install-bin DESTDIR=${D} PREFIX=${prefix}
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/python3-native/python3:${USRBINPATH}/env python3:g' ${D}${bindir}/hg
}
PACKAGES =+ "${PN}-python"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"

FILES:${PN}-python = "${nonarch_libdir}/${PYTHON_DIR}"

