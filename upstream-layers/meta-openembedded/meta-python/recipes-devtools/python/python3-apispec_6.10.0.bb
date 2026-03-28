SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_flit_core

SRC_URI[sha256sum] = "0a888555cd4aa5fb7176041be15684154fd8961055e1672e703abf737e8761bf"

RDEPENDS:${PN} += "python3-packaging"
