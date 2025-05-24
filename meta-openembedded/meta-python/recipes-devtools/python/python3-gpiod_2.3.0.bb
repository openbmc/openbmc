SUMMARY = "Python bindings for libgpiod."

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & CC-BY-SA-4.0"
# The actual license files live in the upstream libgpiod from which the pypi
# package is spun out.
LIC_FILES_CHKSUM = "file://pyproject.toml;beginline=13;endline=13;md5=fca786987ac89b59c767e55467de581e"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "daa840ed5b691e7078a9cf21c79fe813b9a91c3ed0bdbafae0181c7b98b80300"

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
