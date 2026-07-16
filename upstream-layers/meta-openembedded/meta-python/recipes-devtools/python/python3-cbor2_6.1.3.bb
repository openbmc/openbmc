DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "8d70680acb55c04ea5b5ad86da094f9612b53d5a8a65d0f5b3aafc3ce917ecbb"

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
