SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d9c303644a2e62578f0347748a80358"

inherit pypi setuptools3

SRC_URI[sha256sum] = "42b8a6833cf154c9dbd22d006b56bf9c49c972d32d24fe716fd734e0f6b739b8"

RDEPENDS:${PN} += "python3-packaging"
