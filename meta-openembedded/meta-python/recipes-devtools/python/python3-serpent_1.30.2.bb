SUMMARY = "Serialization based on ast.literal_eval"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=5271c65b7cf83bd28055e592c2d14667"

SRC_URI[md5sum] = "cbef4f9c88f88c38195d11a0363a095f"
SRC_URI[sha256sum] = "72753820246a7d8486e8b385353e3bbf769abfceec2e850fa527a288b084ff7a"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytz \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
"    
