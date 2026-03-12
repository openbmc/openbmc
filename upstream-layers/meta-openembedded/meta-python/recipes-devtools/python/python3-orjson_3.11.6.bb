SUMMARY = "orjson is a fast, correct JSON library for Python"
HOMEPAGE = "https://pypi.org/project/orjson/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d"

SRCREV = "ec02024c3837255064f248c0d2d331319b75e9ad"
PYPI_SRC_URI = "git://github.com/ijl/orjson;protocol=https;branch=master;tag=${PV};destsuffix=orjson-${PV}"

CVE_PRODUCT = "orjson"

require ${BPN}-crates.inc

inherit pypi python_maturin cargo-update-recipe-crates ptest-python-pytest

SRC_URI += " \
    file://0001-Guard-avx512-module-with-x86-target-cfg.patch \
    file://0002-Guard-x86-feature-detection-macro-in-pystrref-object.patch \
"
DEPENDS = "python3-maturin-native"

RDEPENDS:${PN} += "python3-maturin python3-mypy"

do_compile:prepend() {
    sed -i "/panic = \"abort\"/d" ${S}/Cargo.toml
}

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/data
    cp -rf ${S}/data/* ${D}${PTEST_PATH}/data/
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}

RDEPENDS:${PN}-ptest += "\
    python3-dateutil \
    python3-dateutil-zoneinfo \
    python3-faker \
    python3-numpy \
    python3-pandas \
    python3-pendulum \
    python3-psutil \
    python3-pytz \
    python3-tzdata \
"

BBCLASSEXTEND = "native nativesdk"
