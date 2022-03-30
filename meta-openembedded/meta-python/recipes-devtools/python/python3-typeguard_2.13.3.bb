SUMMARY = "Run-time type checker for Python"
HOMEPAGE = "https://pypi.org/project/typeguard/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "00edaa8da3a133674796cf5ea87d9f4b4c367d77476e185e80251cc13dfbb8c4"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
        ${PYTHON_PN}-typing-extensions \
        ${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

DEPENDS += "\
    python3-distutils-extra-native \
    python3-setuptools-scm-native \
"

BBCLASSEXTEND = "native nativesdk"
