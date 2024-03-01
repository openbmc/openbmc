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

SRC_URI[sha256sum] = "64d8fadce6a74f478f29832c1eaa2a04856655ebff17292d5237fc8317c3a3c5"

DEPENDS += "python3-six-native"

PYPI_PACKAGE = "pyperf"
inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-misc python3-statistics"
