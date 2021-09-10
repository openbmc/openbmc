SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7c28f460fafe7be454fcdcac0b60263"

SRC_URI[sha256sum] = "10b34e7f8e3207ee6fb70dcdc9bce473851ee3daf0b47c58aec1b48032ac11ce"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytz \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
"    
