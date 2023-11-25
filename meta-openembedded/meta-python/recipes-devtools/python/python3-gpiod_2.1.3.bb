SUMMARY = "Python bindings for libgpiod."

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & CC-BY-SA-4.0"
# The actual license files live in the upstream libgpiod from which the pypi
# package is spun out.
LIC_FILES_CHKSUM = "file://setup.py;beginline=237;endline=237;md5=f5ddb56912b7f20bf94aa4d1bdcd3533"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "a33193d6cab79d252329f71666a35a3668e04f1f82bf9b93ee2c9ae852398b20"

inherit setuptools3-base python_pep517 ptest pypi

DEPENDS += " \
    libgpiod \
    python3-setuptools-native \
"
RDEPENDS:${PN} += " \
    libgpiod (>= 2.1) \
    python3-datetime \
    python3-profile \
"
RDEPENDS:${PN}-ptest += " \
    libgpiod-ptest \
    python3-setuptools \
"

export LINK_SYSTEM_LIBGPIOD = "1"

do_compile_ptest() {
    setup_target_config
    nativepython3 build_tests.py
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -d ${D}${PTEST_PATH}/gpiod-test/procname
    install -m 0644 ${S}/tests/*.py ${D}${PTEST_PATH}/gpiod-test/
    install -m 0644 ${B}/tests/gpiosim/_ext* ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${S}/tests/gpiosim/*.py ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${B}/tests/procname/_ext* ${D}${PTEST_PATH}/gpiod-test/procname
    install -m 0644 ${S}/tests/procname/*.py ${D}${PTEST_PATH}/gpiod-test/procname
}

INSANE_SKIP:${PN}-ptest += "buildpaths"
