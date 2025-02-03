require libgpiod.inc

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI += "file://0001-bindings-cxx-disable-tests.patch"

SRC_URI[sha256sum] = "ae280f697bf035a1fb780c9972e5c81d0d2712b7ab6124fb3fba24619daa72bc"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-1.x:"

inherit python3native

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev glib-2.0 catch2"
PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"

PACKAGES =+ "${PN}-python"
FILES:${PN}-tools += "${bindir}/gpiofind"
FILES:${PN}-ptest += " \
    ${bindir}/gpiod_py_test.py \
    ${libdir}/libgpiomockup.so.* \
"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*.so"
FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"

RRECOMMENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '', d)}"
RRECOMMENDS:${PN}-ptest += " \
    kernel-module-gpio-mockup \
    ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-unittest', '', d)} \
"
RDEPENDS:${PN}-ptest += " \
    python3-packaging \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'bats', '', d)} \
"

do_install_ptest:append() {
    install -m 0755 ${S}/tools/gpio-tools-test ${D}${PTEST_PATH}/tests/
    install -m 0755 ${S}/tools/gpio-tools-test.bats ${D}${PTEST_PATH}/tests/

    if ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'true', 'false', d)}; then
        install -m 0755 ${S}/bindings/python/tests/gpiod_py_test.py ${D}${PTEST_PATH}/tests/
    fi
}
