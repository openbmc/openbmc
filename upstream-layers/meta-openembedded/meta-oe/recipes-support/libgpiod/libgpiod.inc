SUMMARY = "C library and tools for interacting with the linux GPIO character device"
HOMEPAGE = "https://libgpiod.readthedocs.io/"
RECIPE_MAINTAINER = "Bartosz Golaszewski <brgl@bgdev.pl>"

inherit pkgconfig ptest

SRC_URI += " \
    https://www.kernel.org/pub/software/libs/libgpiod/libgpiod-${PV}.tar.xz \
    file://run-ptest \
"

# Enable cxx bindings by default.
PACKAGECONFIG ?= " \
    cxx \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"

PACKAGES =+ "${PN}-tools libgpiodcxx"
FILES:${PN}-tools += " \
    ${bindir}/gpiodetect \
    ${bindir}/gpioinfo \
    ${bindir}/gpioget \
    ${bindir}/gpioset \
    ${bindir}/gpiomon \
"
FILES:libgpiodcxx = "${libdir}/libgpiodcxx.so.*"

RRECOMMENDS:${PN}-ptest += "coreutils"

do_install:append() {
    rm -f ${D}${bindir}/gpiod-test
    rm -f ${D}${bindir}/gpio-tools-test
    rm -f ${D}${bindir}/gpio-tools-test.bats
    rm -f ${D}${bindir}/gpiod-cxx-test
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/
}
