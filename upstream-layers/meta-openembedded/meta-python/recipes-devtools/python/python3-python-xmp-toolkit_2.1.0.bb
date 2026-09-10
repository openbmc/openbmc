SUMMARY = "XMP I/O wrapping Exempi"
DESCRIPTION = "XMP I/O wrapping Exempi"
HOMEPAGE = "https://github.com/python-xmp-toolkit/python-xmp-toolkit"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=30fa74de63728af31f13dc3bb8cb1756"

SRC_URI[sha256sum] = "ca0aa2c60d418dd2558767db59953ab5954fb5b87dc0b50cecd60566b0b4e2da"

inherit pypi python_flit_core ptest-python-pytest

PYPI_PACKAGE = "python_xmp_toolkit"

DEPENDS += "\
    python3-pytz-native \
"
RDEPENDS:${PN} = "\
    exempi \
    python3-pytz \
"

RDEPENDS:${PN}-ptest += "\
    python3-core \
    python3-pytest-cov \
"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}

BBCLASSEXTEND = "native nativesdk"
