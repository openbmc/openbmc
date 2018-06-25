SUMMARY = "The Mercurial distributed SCM"
HOMEPAGE = "http://mercurial.selenic.com/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "python python-native"
DEPENDS_class-native = "python-native"
RDEPENDS_${PN} = "python python-modules"

inherit python-dir

SRC_URI = "https://www.mercurial-scm.org/release/${BP}.tar.gz"
SRC_URI[md5sum] = "c1d9fad1b7ed7077b0d4ae82e71154db"
SRC_URI[sha256sum] = "234af4a67565c85923b0a1910c704ab44bcf12f69b85532687208776563d87de"

S = "${WORKDIR}/mercurial-${PV}"

BBCLASSEXTEND = "native"

EXTRA_OEMAKE = "STAGING_LIBDIR=${STAGING_LIBDIR} STAGING_INCDIR=${STAGING_INCDIR} \
    PREFIX=${prefix}"

do_configure_append () {
    sed -i -e 's:PYTHON=python:PYTHON=${STAGING_BINDIR_NATIVE}/python-native/python:g' ${S}/Makefile
}

do_install () {
    oe_runmake -e install-bin DESTDIR=${D} PREFIX=${prefix}
}

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"
