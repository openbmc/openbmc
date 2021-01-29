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

SRC_URI[sha256sum] = "1257d673d89fdcdbaec8077afeb365e7a94739c1b263572b09403cac25708ad3"

DEPENDS += "${PYTHON_PN}-six-native"

PYPI_PACKAGE = "pyperf"
inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-misc"
