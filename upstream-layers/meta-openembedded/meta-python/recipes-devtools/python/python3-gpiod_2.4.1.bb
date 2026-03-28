SUMMARY = "Python bindings for libgpiod."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b54a1fd55a448865a0b32d41598759d"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "d29a1e8b2a065f7ed82f00a96009bc1486fc705bb2ad25820a8ae962ec6d7688"

inherit python_setuptools_build_meta python_pep517 ptest pypi

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
    libgpiod-gpiosim \
    python3-setuptools \
"

export LINK_SYSTEM_LIBGPIOD = "1"

do_compile_ptest() {
    setup_target_config
    nativepython3 build_tests.py
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -d ${D}${PTEST_PATH}/gpiod-test/system
    install -m 0644 ${S}/tests/*.py ${D}${PTEST_PATH}/gpiod-test/
    install -m 0644 ${B}/tests/gpiosim/_ext* ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${S}/tests/gpiosim/*.py ${D}${PTEST_PATH}/gpiod-test/gpiosim
    install -m 0644 ${B}/tests/system/_ext* ${D}${PTEST_PATH}/gpiod-test/system
    install -m 0644 ${S}/tests/system/*.py ${D}${PTEST_PATH}/gpiod-test/system
}

INSANE_SKIP:${PN}-ptest += "buildpaths"
