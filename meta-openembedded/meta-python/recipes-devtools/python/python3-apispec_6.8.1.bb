SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f4916cbb7be156963b18f5929a0e42bd2349135834b680a81b12432bcfaa9a39"

RDEPENDS:${PN} += "python3-packaging"
