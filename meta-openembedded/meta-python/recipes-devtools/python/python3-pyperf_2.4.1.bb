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

SRC_URI[sha256sum] = "38cf5e90c56f906a8320ce82a50bfa92c902b93affd72e4dc81580115f355853"

DEPENDS += "${PYTHON_PN}-six-native"

PYPI_PACKAGE = "pyperf"
inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-misc ${PYTHON_PN}-statistics"
