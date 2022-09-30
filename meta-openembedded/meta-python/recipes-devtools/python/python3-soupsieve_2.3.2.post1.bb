SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=c7a2acf04248c0d02dac4c82ee8a7f56"

SRC_URI[sha256sum] = "fc53893b3da2c33de295667a0e19f078c14bf86544af307354de5fcf12a3f30d"

inherit pypi python_hatchling python_setuptools_build_meta ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN} += "\
        ${PYTHON_PN}-beautifulsoup4 \
"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
        ${PYTHON_PN}-beautifulsoup4 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
