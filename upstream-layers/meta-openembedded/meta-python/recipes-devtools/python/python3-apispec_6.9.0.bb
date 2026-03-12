SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "7a38ce7c3eedc7771e6e33295afdd8c4b0acdd9865b483f8cf6cc369c93e8d1e"

RDEPENDS:${PN} += "python3-packaging"
