SUMMARY = "A library for property-based testing"
HOMEPAGE = "https://github.com/HypothesisWorks/hypothesis/tree/master/hypothesis-python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4ee62c16ebd0f4f99d906f36b7de8c3c"

PTEST_PYTEST_DIR ?= "examples"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python_maturin ptest-python-pytest

CARGO_SRC_DIR = "rust"

SRC_URI += " \
    file://test_binary_search.py \
    file://test_rle.py \
    "

SRC_URI[sha256sum] = "23a60707a2bc5e0c59ef95e94264c4e8fd1c54b4ca1b95cbde74fb68b9c2eeef"

RDEPENDS:${PN} += " \
    python3-attrs \
    python3-compression \
    python3-core \
    python3-json \
    python3-pytest \
    python3-sortedcontainers \
    python3-statistics \
    python3-unittest \
    python3-zoneinfo \
    "

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}
    install -m 0755 ${UNPACKDIR}/test_binary_search.py ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}/
    install -m 0755 ${UNPACKDIR}/test_rle.py ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}/
}

BBCLASSEXTEND = "native nativesdk"
