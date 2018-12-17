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
SRC_URI[md5sum] = "f9b2e4a3b5901ef744fa3abe4196e97e"
SRC_URI[sha256sum] = "89fa8ecbc8aa6e48e98f9803a1683ba91367124295dba2407b28c34ca621108d"

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
