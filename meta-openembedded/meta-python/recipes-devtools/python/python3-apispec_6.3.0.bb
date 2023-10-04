SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=824d596e050c0d5e3361efb438425945"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6cb08d92ce73ff0b3bf46cb2ea5c00d57289b0f279fb0256a3df468182ba5344"

RDEPENDS:${PN} += "python3-packaging"
