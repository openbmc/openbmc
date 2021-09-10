# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A ``pytest`` fixture for benchmarking code. It will group the tests into rounds that are calibrated to the chosen timer."
HOMEPAGE = "https://github.com/ionelmc/pytest-benchmark"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

SRC_URI[sha256sum] = "40e263f912de5a81d891619032983557d62a3d85843f9a9f30b98baea0cd7b47"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core python3-py-cpuinfo python3-pytest python3-aspectlib"

BBCLASSEXTEND = "native nativesdk"
