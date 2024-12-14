SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "c01b8b6ff40ffedf55b79a67f9dd920e9b2fc3909aae116facf6c8372a08b933"

RDEPENDS:${PN} += "python3-packaging"
