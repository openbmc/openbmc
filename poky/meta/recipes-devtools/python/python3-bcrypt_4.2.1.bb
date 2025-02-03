SUMMARY = "Modern password hashing for your software and your servers."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f7bb094c7232b058c7e9f2e431f389c"
HOMEPAGE = "https://pypi.org/project/bcrypt/"

DEPENDS += "python3-cffi-native"
LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', '-fuse-ld=bfd', '', d)}"

SRC_URI[sha256sum] = "6765386e3ab87f569b276988742039baab087b2cdb01e809d74e74503c2faafe"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates ptest-python-pytest

CARGO_SRC_DIR = "src/_bcrypt"

require ${BPN}-crates.inc

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}:class-target += "\
    python3-cffi \
    python3-ctypes \
    python3-shell \
"
