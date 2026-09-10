SUMMARY = "A simple wrapper around optparse for powerful command line utilities."
DESCRIPTION = "\
Click is a Python package for creating beautiful command line interfaces \
in a composable way with as little code as necessary. It is the 'Command \
Line Interface Creation Kit'. It is highly configurable but comes with \
sensible defaults out of the box."
HOMEPAGE = "http://click.pocoo.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1fa98232fd645608937a0fdc82e999b8"

SRC_URI[sha256sum] = "ba0d2089de75ea0310e2dde03160e6ca10009947fb95a182f9b54021bb272e34"

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
    "
RDEPENDS:${PN}-ptest += "coreutils less"

BBCLASSEXTEND = "native nativesdk"
