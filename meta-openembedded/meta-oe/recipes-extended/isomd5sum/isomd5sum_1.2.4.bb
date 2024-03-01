SUMMARY = "Tools for taking the MD5 sum of ISO images"
DESCRIPTION = "Tools for taking the MD5 sum of ISO images"

DEPENDS = "popt python3 openssl curl popt-native"
RDEPENDS:${BPN} = "openssl curl"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "git://github.com/rhinstaller/isomd5sum.git;branch=master;protocol=https \
           file://0001-tweak-install-prefix.patch \
           file://0002-fix-parallel-error.patch \
"

S = "${WORKDIR}/git"
inherit python3native

EXTRA_OEMAKE += " \
    DESTDIR='${D}' \
    PYTHONINCLUDE='-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}' \
    PYTHONSITEPACKAGES='${PYTHON_SITEPACKAGES_DIR}' \
"

do_install () {
    oe_runmake install
}

PACKAGES += "python3-${BPN}"

RPROVIDES:${BPN}-dbg += "python3-${BPN}-dbg"

FILES:python3-${BPN} = "${PYTHON_SITEPACKAGES_DIR}/pyisomd5sum.so"

SRCREV = "3f4c9bd3f21ec9ac75a025dfa3fa30fe3f621831"

BBCLASSEXTEND = "native"
