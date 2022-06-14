SUMMARY = "C library and tools for interacting with the linux GPIO character device"
AUTHOR = "Bartosz Golaszewski <brgl@bgdev.pl>"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = " \
    https://www.kernel.org/pub/software/libs/${BPN}/${BP}.tar.xz \
    file://run-ptest \
"

SRC_URI[md5sum] = "28e79f6f70fee1da9079558d8b7b3736"
SRC_URI[sha256sum] = "841be9d788f00bab08ef22c4be5c39866f0e46cb100a3ae49ed816ac9c5dddc7"

inherit autotools pkgconfig python3native ptest

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev glib-2.0 catch2"
PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"

# Enable cxx bindings by default.
PACKAGECONFIG ?= "cxx"

# Always build tools - they don't have any additional
# requirements over the library.
EXTRA_OECONF = "--enable-tools"

DEPENDS += "autoconf-archive-native"

PACKAGES =+ "${PN}-tools libgpiodcxx ${PN}-python"
FILES:${PN}-tools = " \
    ${bindir}/gpiodetect \
    ${bindir}/gpioinfo \
    ${bindir}/gpioget \
    ${bindir}/gpioset \
    ${bindir}/gpiofind \
    ${bindir}/gpiomon \
"
FILES:${PN}-ptest += " \
    ${bindir}/gpiod-test \
    ${bindir}/gpio-tools-test \
    ${bindir}/gpio-tools-test.bats \
    ${bindir}/gpiod-cxx-test \
    ${bindir}/gpiod_py_test.py \
    ${libdir}/libgpiomockup.so.* \
"
FILES:libgpiodcxx = "${libdir}/libgpiodcxx.so.*"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*.so"
FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"

RRECOMMENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '', d)}"
RRECOMMENDS:${PN}-ptest += " \
    kernel-module-gpio-mockup \
    coreutils \
    ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-unittest', '', d)} \
"
RDEPENDS:${PN}-ptest += "bats python3-packaging"

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'tests', '', d)}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    # These are the core C library tests
    install -m 0755 ${B}/tests/.libs/gpiod-test ${D}${PTEST_PATH}/tests/

    # Tools are always built so let's always install them for ptest even if
    # we're not selecting libgpiod-tools.
    install -m 0755 ${S}/tools/gpio-tools-test ${D}${PTEST_PATH}/tests/
    install -m 0755 ${S}/tools/gpio-tools-test.bats ${D}${PTEST_PATH}/tests/
    for tool in ${FILES:${PN}-tools}; do
        install ${B}/tools/.libs/$(basename $tool) ${D}${PTEST_PATH}/tests/
    done

    if ${@bb.utils.contains('PACKAGECONFIG', 'cxx', 'true', 'false', d)}; then
        install -m 0755 ${B}/bindings/cxx/tests/.libs/gpiod-cxx-test ${D}${PTEST_PATH}/tests/
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'true', 'false', d)}; then
        install -m 0755 ${S}/bindings/python/tests/gpiod_py_test.py ${D}${PTEST_PATH}/tests/
    fi
}
