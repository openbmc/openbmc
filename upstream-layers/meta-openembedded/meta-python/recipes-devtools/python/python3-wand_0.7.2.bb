SUMMARY = "Ctypes-based simple MagickWand API binding for Python"
HOMEPAGE = "https://docs.wand-py.org/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a2b05e6331064556e971cfa0efca0bf"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "0387fd08848d00cadd1d885fcb19a17dd4250df2029e5338b6b668b7fed64b5b"

inherit pypi python_setuptools_build_meta ptest

PYPI_PACKAGE = "wand"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

FILES:${PN}-doc += "${datadir}/README.rst"

RDEPENDS:${PN} += " \
    imagemagick \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-datetime \
    python3-numbers \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -f ${S}/pyproject.toml ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
