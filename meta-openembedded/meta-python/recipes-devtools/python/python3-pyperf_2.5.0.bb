SUMMARY = "A toolkit to write, run and analyze benchmarks"
DESCRIPTION = " \
The Python pyperf module is a toolkit to write, run and analyze benchmarks. \
Features: \
    * Simple API to run reliable benchmarks \
    * Automatically calibrate a benchmark for a time budget. \
    * Spawn multiple worker processes. \
    * Compute the mean and standard deviation. \
    * Detect if a benchmark result seems unstable. \
    * JSON format to store benchmark results. \
    * Support multiple units: seconds, bytes and integer. \
"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=78bc2e6e87c8c61272937b879e6dc2f8"

SRC_URI[sha256sum] = "9fd9be5b57224e68b5a5b88f7126f15b6c8667573f62a0a39faf14d6fdd13909"

DEPENDS += "${PYTHON_PN}-six-native"

PYPI_PACKAGE = "pyperf"
inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-misc ${PYTHON_PN}-statistics"
