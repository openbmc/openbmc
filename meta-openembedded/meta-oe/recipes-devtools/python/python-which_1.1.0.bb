DESCRIPTION = "which.py -- a portable GNU which replacement"
HOMEPAGE = "http://code.google.com/p/which/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=030b09798681482b9ad12ac47be496ea"

inherit setuptools pypi python-dir

SRCREV = "425bdeeb2d87c36e2313dc4b8d69ff2bb5a02ee9"
PYPI_SRC_URI = "git://github.com/trentm/which"

S = "${WORKDIR}/git"

do_install_append() {
    rmdir -p --ignore-fail-on-non-empty ${D}${STAGING_BINDIR_NATIVE}
    rmdir -p --ignore-fail-on-non-empty ${D}${datadir}
}

BBCLASSEXTEND = "native nativesdk"
