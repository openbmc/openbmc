DESCRIPTION = "Bindings for the scrypt key derivation function library"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=514fe4e05afed341c690c453fc87b10b"
HOMEPAGE = "https://github.com/holgern/py-scrypt"

SRC_URI += "file://0001-py-scrypt-remove-the-hard-coded-include-paths.patch"

SRC_URI[sha256sum] = "0d212010ba8c2e55475ba6258f30cee4da0432017514d8f6e855b7f1f8c55c77"

inherit pypi ptest-python-pytest setuptools3 dos2unix

RDEPENDS:${PN} += " \
    python3-ctypes \
"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/scrypt/tests/* ${D}${PTEST_PATH}/tests/
}
