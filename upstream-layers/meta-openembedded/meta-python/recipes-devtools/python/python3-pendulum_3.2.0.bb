SUMMARY = "Python datetimes made easy"
DESCRIPTION = "Pendulum is a Python package to ease datetimes manipulation. \
It provides classes that are drop-in replacements for the native ones (they \
inherit from them). \
Special care has been taken to ensure timezones are handled correctly, and \
are based on the underlying tzinfo implementation. For example, all \
comparisons are done in UTC or in the timezone of the datetime being used. \
The default timezone, except when using the now(), method will always be UTC. \
"
HOMEPAGE = "https://pendulum.eustace.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=869e1c364438f234f09673c4039ed114"

DEPENDS = "python3-maturin-native"

SRCREV = "aea611d7a1c15ed0da56505c3f370fe4446ba733"
PYPI_SRC_URI = "git://github.com/python-pendulum/pendulum;protocol=https;branch=master;tag=${PV};destsuffix=pendulum-${PV}"

SRC_URI += "file://0001-rust-Cargo.toml-inhibit-strip.patch"

require ${BPN}-crates.inc

inherit pypi python_maturin cargo-update-recipe-crates ptest-python-pytest

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "python3-dateutil python3-tzdata"

RDEPENDS:${PN}-ptest += "\
    python3-dateutil-zoneinfo \
    python3-time-machine \
    "
