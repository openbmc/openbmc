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

SRC_URI[md5sum] = "e30b67b62a4e30e4504352d0778c622c"
SRC_URI[sha256sum] = "0d214aa65e085d3e4108a36152cb12f7cd0f4e7fda93b5134b43a9687c975786"

inherit pypi

PYPI_PACKAGE = "pyperf"

DEPENDS += "${PYTHON_PN}-six-native"
