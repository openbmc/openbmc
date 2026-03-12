SUMMARY = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b4fef6e4b0828c2401fb983363985b39"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI[sha256sum] = "ec6652a1bee61c53a3e5776b6049172c53b6aaba34f18c9ad04f82712bac623d"

RDEPENDS:${PN}-ptest += " \
    python3-objgraph \
    python3-psutil \
    python3-pytest-cov \
    python3-pytest-codspeed \
"

BBCLASSEXTEND = "native nativesdk"
