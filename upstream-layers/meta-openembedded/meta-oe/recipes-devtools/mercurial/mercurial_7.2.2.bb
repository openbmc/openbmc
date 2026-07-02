SUMMARY = "The Mercurial distributed SCM"
HOMEPAGE = "http://mercurial.selenic.com/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "python3 python3-native python3-setuptools-native python3-setuptools-scm-native"
RDEPENDS:${PN} = "python3 python3-modules"

inherit python3native python3targetconfig

SRC_URI = "https://www.mercurial-scm.org/release/${BP}.tar.gz"
SRC_URI[sha256sum] = "f2ec8e7eeef0500591706d374555f0ceb118822068e75fa3b32be07dd2184f6c"

S = "${UNPACKDIR}/mercurial-${PV}"

BBCLASSEXTEND = "native"

export LDSHARED = "${CCLD} -shared"

# Mercurial's setup.py derives its version via setuptools-scm; the release
# tarball is not a VCS checkout, so tell setuptools-scm the version explicitly.
export SETUPTOOLS_SCM_PRETEND_VERSION = "${PV}"

EXTRA_OEMAKE = "STAGING_LIBDIR=${STAGING_LIBDIR} STAGING_INCDIR=${STAGING_INCDIR} \
    PREFIX=${prefix}"

do_configure:append () {
    sed -i -e 's:^PYTHON?*=.*:PYTHON=python3:g' ${S}/Makefile
}

# Mercurial 7.x removed the "make build"/"make all" targets and reworked
# "make install-bin" to use "pip install", which needs pip in the sysroot and
# ignores DESTDIR. Drive setup.py directly instead (the legacy, DESTDIR-aware
# path). python3targetconfig makes setup.py cross-compile the C extensions
# with the target toolchain.
do_compile () {
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build
}

do_install () {
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py install \
        --skip-build --root=${D} --prefix=${prefix} \
        --install-lib=${PYTHON_SITEPACKAGES_DIR} \
        --single-version-externally-managed
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/python3-native/python3:${USRBINPATH}/env python3:g' ${D}${bindir}/hg
}
PACKAGES =+ "${PN}-python"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR} ${datadir}"
FILES:${PN}-python = "${nonarch_libdir}/${PYTHON_DIR}"

CVE_PRODUCT = "mercurial-scm:mercurial mercurial:mercurial"
