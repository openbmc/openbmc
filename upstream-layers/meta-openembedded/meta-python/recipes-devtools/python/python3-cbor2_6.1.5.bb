DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "6eb06160c42315ac0c4ded461c7d84d92fa18c69d13d17fc1dfc1fae96580c95"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates ptest-python-pytest

CARGO_SRC_DIR = "rust"

require ${BPN}-crates.inc

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
