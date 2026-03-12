DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "b19c35fcae9688ac01ef75bad5db27300c2537eb4ee00ed07e05d8456a0d4931"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
    python3-unixadmin \
"
RDEPENDS:${PN} += " \
    python3-datetime \
"

CVE_PRODUCT = "cbor2"

BBCLASSEXTEND = "native nativesdk"
