DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "6b43037a66947dee5af0abb1a4c3a13b3abac5a4a3f32f9771efbbcd030fd909"

inherit pypi python_setuptools3_rust cargo-update-recipe-crates ptest-python-pytest

CARGO_SRC_DIR = "rust"

require ${BPN}-crates.inc

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
    python3-unixadmin \
"
RDEPENDS:${PN} += " \
    python3-datetime \
"

CVE_PRODUCT = "cbor2"

BBCLASSEXTEND = "native nativesdk"
