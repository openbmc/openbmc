SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "46896cb0cab23b4e853e528528937719739f836358832bb8610edd14db1d9c01"

RDEPENDS:${PN} += "python3-packaging"
