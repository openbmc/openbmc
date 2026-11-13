DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "3b08ba9f3a556186515000949d478e47e6d55322f9d86bdaef5b4e3d27d48298"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates ptest-python-pytest

CARGO_SRC_DIR = "rust"

require ${BPN}-crates.inc

do_install_ptest:append() {
    install -Dm 0644 ${S}/docs/usage.rst ${D}${PTEST_PATH}/docs/usage.rst
}

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
    python3-tzdata \
    python3-unixadmin \
    python3-zoneinfo \
"
RDEPENDS:${PN} += " \
    python3-datetime \
"

CVE_PRODUCT = "agronholm:cbor2"

BBCLASSEXTEND = "native nativesdk"
