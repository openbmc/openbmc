SUMMARY = "Declarative parsing and validation of HTTP request objects, with built-in support for popular web frameworks."
HOMEPAGE = "https://github.com/marshmallow-code/webargs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c3ff8567ef1f2a8cf223f30ac5a6f094"

inherit pypi setuptools3

SRC_URI[sha256sum] = "cab207941b0686c4d086c823632ddcd4343151644341a32fcf50b8eaa71e31c7"

RDEPENDS:${PN} += "\
    python3-marshmallow \
    python3-packaging \
    "
