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

SRC_URI[md5sum] = "d67fe5f447963da8873f3e9923f76de1"
SRC_URI[sha256sum] = "8d0143a22a13ee10c997a648f30b82cdc40175d5a20b11055ae058a82e45d371"

inherit pypi

PYPI_PACKAGE = "pyperf"

DEPENDS += "${PYTHON_PN}-six-native"
