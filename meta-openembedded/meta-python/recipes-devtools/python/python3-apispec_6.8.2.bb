SUMMARY = "A pluggable API specification generator. Currently supports the OpenAPI Specification (f.k.a. the Swagger specification)."
HOMEPAGE = "https://github.com/marshmallow-code/apispec"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a75956865b40c80a37c1e864716592b4"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "ce5b69b9fcf0250cb56ba0c1a52a75ff22c2f7c586654e57884399018c519f26"

RDEPENDS:${PN} += "python3-packaging"
