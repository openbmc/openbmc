SUMMARY = "A ``pytest`` fixture for benchmarking code. It will group the tests into rounds that are calibrated to the chosen timer."
HOMEPAGE = "https://github.com/ionelmc/pytest-benchmark"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d907de309286ace60c424a81666867d"

SRC_URI[sha256sum] = "c1ee70a58f9bfb1ac955cc634247a340b0df7bcb949071cfca396f16c6fc3516"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-aspectlib \
    python3-core \
    python3-datetime \
    python3-json \
    python3-misc \
    python3-netclient \
    python3-numbers \
    python3-profile \
    python3-py-cpuinfo \
    python3-pytest \
    python3-statistics \
"

BBCLASSEXTEND = "native nativesdk"
