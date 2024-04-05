DESCRIPTION = "Tomli-W is a Python library for writing TOML. It is a write-only counterpart to Tomli, which is a read-only TOML parser."
HOMEPAGE = "https://github.com/hukkin/tomli-w"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5"

SRCREV = "19099125f32e7c491603e393263754262b356956"
PYPI_SRC_URI = "git://github.com/hukkin/tomli-w.git;protocol=https;branch=master"

inherit pypi python_flit_core ptest

S = "${WORKDIR}/git"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        python3-pytest \
        python3-tomli \
        python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        install -d ${D}${PTEST_PATH}/benchmark
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        cp -rf ${S}/benchmark/* ${D}${PTEST_PATH}/benchmark/
}

RDEPENDS:${PN} += " \
        python3-datetime \
        python3-numbers \
        python3-stringold \
"
