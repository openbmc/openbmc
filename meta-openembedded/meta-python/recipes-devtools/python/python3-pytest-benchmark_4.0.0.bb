SUMMARY = "A ``pytest`` fixture for benchmarking code. It will group the tests into rounds that are calibrated to the chosen timer."
HOMEPAGE = "https://github.com/ionelmc/pytest-benchmark"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7d2e9d24c2b5bad57ca894da972e22e"

SRC_URI[sha256sum] = "fb0785b83efe599a6a956361c0691ae1dbb5318018561af10f3e915caa0048d1"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core python3-py-cpuinfo python3-pytest python3-aspectlib"

BBCLASSEXTEND = "native nativesdk"
