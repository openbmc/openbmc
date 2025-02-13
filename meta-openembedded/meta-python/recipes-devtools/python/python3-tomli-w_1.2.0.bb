DESCRIPTION = "Tomli-W is a Python library for writing TOML. It is a write-only counterpart to Tomli, which is a read-only TOML parser."
HOMEPAGE = "https://github.com/hukkin/tomli-w"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaaaf0879d17df0110d1aa8c8c9f46f5"

SRCREV = "a8f80172ba16fe694e37f6e07e6352ecee384c58"
PYPI_SRC_URI = "git://github.com/hukkin/tomli-w.git;protocol=https;branch=master"

inherit pypi python_flit_core ptest-python-pytest

S = "${WORKDIR}/git"

RDEPENDS:${PN}-ptest += " \
        python3-tomli \
"

do_install_ptest:append() {
        install -d ${D}${PTEST_PATH}/benchmark
        cp -rf ${S}/benchmark/* ${D}${PTEST_PATH}/benchmark/
}

RDEPENDS:${PN} += " \
        python3-datetime \
        python3-numbers \
        python3-stringold \
"
