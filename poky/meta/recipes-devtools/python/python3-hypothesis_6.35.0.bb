SUMMARY = "A library for property-based testing"
HOMEPAGE = "https://github.com/HypothesisWorks/hypothesis/tree/master/hypothesis-python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4ee62c16ebd0f4f99d906f36b7de8c3c"

PYPI_PACKAGE = "hypothesis"

inherit pypi setuptools3 ptest

SRC_URI += " \
    file://run-ptest \
    file://test_binary_search.py \
    file://test_rle.py \
    "

SRC_URI[sha256sum] = "ce3961fff61e7353d022608788cbc9876c293d2468749eeba27511adc9565131"

RDEPENDS:${PN} += " \
    python3-attrs \
    python3-compression \
    python3-core \
    python3-json \
    python3-sortedcontainers \
    python3-statistics \
    python3-unittest \
    "

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/examples
    install -m 0755 ${WORKDIR}/test_binary_search.py ${D}${PTEST_PATH}/examples/
    install -m 0755 ${WORKDIR}/test_rle.py ${D}${PTEST_PATH}/examples/
}

BBCLASSEXTEND = "native nativesdk"
