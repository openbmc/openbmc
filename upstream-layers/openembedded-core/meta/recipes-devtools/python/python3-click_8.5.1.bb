SUMMARY = "A simple wrapper around optparse for powerful command line utilities."
DESCRIPTION = "\
Click is a Python package for creating beautiful command line interfaces \
in a composable way with as little code as necessary. It is the 'Command \
Line Interface Creation Kit'. It is highly configurable but comes with \
sensible defaults out of the box."
HOMEPAGE = "http://click.pocoo.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1fa98232fd645608937a0fdc82e999b8"

SRC_URI[sha256sum] = "9bbfada76703fd1d784b83eea012e3e8c6a001a876543ef104fa56aaaeb072b4"

inherit pypi python_flit_core ptest-python-pytest

CVE_PRODUCT = "palletsprojects:click"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-terminal \
	python3-unixadmin \
"

do_install_ptest:append() {
    cp -rf ${S}/pyproject.toml ${D}${PTEST_PATH}/
    cp -rf ${S}/docs ${D}${PTEST_PATH}/
}

RDEPENDS:${PN} += "\
    python3-io \
    python3-threading \
    python3-shell \
    "
RDEPENDS:${PN}-ptest += "coreutils less"

BBCLASSEXTEND = "native nativesdk"
