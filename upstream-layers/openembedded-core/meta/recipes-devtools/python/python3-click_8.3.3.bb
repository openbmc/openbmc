SUMMARY = "A simple wrapper around optparse for powerful command line utilities."
DESCRIPTION = "\
Click is a Python package for creating beautiful command line interfaces \
in a composable way with as little code as necessary. It's the "Command \
Line Interface Creation Kit". It's highly configurable but comes with \
sensible defaults out of the box."
HOMEPAGE = "http://click.pocoo.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1fa98232fd645608937a0fdc82e999b8"

SRC_URI[sha256sum] = "398329ad4837b2ff7cbe1dd166a4c0f8900c3ca3a218de04466f38f6497f18a2"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-terminal \
	python3-unixadmin \
"

do_install_ptest:append() {
    cp -rf ${S}/pyproject.toml ${D}${PTEST_PATH}/
    cp -rf ${S}/docs ${D}${PTEST_PATH}/
}

CLEANBROKEN = "1"

RDEPENDS:${PN} += "\
    python3-io \
    python3-threading \
    "
RDEPENDS:${PN}-ptest += "coreutils less"

BBCLASSEXTEND = "native nativesdk"
