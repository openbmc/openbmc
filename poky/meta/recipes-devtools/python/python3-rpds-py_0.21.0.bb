SUMMARY = "Python bindings to the Rust rpds crate for persistent data structures."
HOMEPAGE = "https://pypi.org/project/rpds-py/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7767fa537c4596c54141f32882c4a984"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "ed6378c9d66d0de903763e7706383d60c33829581f0adff47b6535f1802fa6db"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_maturin ptest

PYPI_PACKAGE = "rpds_py"

RDEPENDS:${PN}-ptest += " \
    python3-iniconfig \
    python3-packaging \
    python3-pluggy \
    python3-pytest \
    python3-unittest-automake-output \
    "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
