SUMMARY = "Python bindings for libgpiod."

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & CC-BY-SA-4.0"
# The actual license files live in the upstream libgpiod from which the pypi
# package is spun out.
LIC_FILES_CHKSUM = "file://setup.py;beginline=57;endline=57;md5=120334ed83ec0cb248605e775bd13b87"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "d5c0f9502e97e7359b9dd601e29f15d668983ce50f5c13323f3302845f790fa6"

inherit setuptools3 ptest pypi

PYPI_PACKAGE = "libgpiod"

DEPENDS += "libgpiod"
RDEPENDS:${PN} += " \
    libgpiod (>= 2.0) \
    python3-datetime \
    python3-profile \
"
RDEPENDS:${PN}-ptest += " \
    libgpiod-ptest \
    python3-setuptools \
"

export GPIOD_WITH_TESTS = "${@bb.utils.contains("PTEST_ENABLED", "1", "1", "0", d)}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -d ${D}${PTEST_PATH}/gpiod-test/procname
    install -m 0644 ${S}/tests/*.py ${D}${PTEST_PATH}/gpiod-test/
    install -m 0644 ${B}/build/lib*/tests/gpiosim/_ext* ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${S}/tests/gpiosim/*.py ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${B}/build/lib*/tests/procname/_ext* ${D}${PTEST_PATH}/gpiod-test/procname
    install -m 0644 ${S}/tests/procname/*.py ${D}${PTEST_PATH}/gpiod-test/procname
}
