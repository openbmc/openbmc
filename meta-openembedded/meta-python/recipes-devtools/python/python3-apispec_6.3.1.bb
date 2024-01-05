SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=824d596e050c0d5e3361efb438425945"

inherit pypi setuptools3

SRC_URI[sha256sum] = "b38e4479916d43f2b1e88ce15fc2fae93adf2e8d55cb59ec74ac66a827941483"

RDEPENDS:${PN} += "python3-packaging"
