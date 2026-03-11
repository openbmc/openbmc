SUMMARY = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b4fef6e4b0828c2401fb983363985b39"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PV .= "+git"

SRCREV = "dbd686f37c1bc14355dce9c16dbbfae82f11dc00"
PYPI_SRC_URI = "git://github.com/aio-libs/multidict;branch=master;protocol=https"
S = "${UNPACKDIR}/python3-multidict-${PV}"

RDEPENDS:${PN}-ptest += " \
    python3-objgraph \
    python3-pytest-cov \
    python3-pytest-codspeed \
"

BBCLASSEXTEND = "native nativesdk"
