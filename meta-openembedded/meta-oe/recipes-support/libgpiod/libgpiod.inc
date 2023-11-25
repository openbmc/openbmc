SUMMARY = "C library and tools for interacting with the linux GPIO character device"
RECIPE_MAINTAINER = "Bartosz Golaszewski <brgl@bgdev.pl>"

inherit autotools pkgconfig ptest

SRC_URI += " \
    https://www.kernel.org/pub/software/libs/libgpiod/libgpiod-${PV}.tar.xz \
    file://run-ptest \
"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"

# Enable cxx bindings by default.
PACKAGECONFIG ?= " \
    cxx \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"

# Always build tools - they don't have any additional
# requirements over the library.
EXTRA_OECONF = "--enable-tools"

DEPENDS += "autoconf-archive-native"

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

    # These are the core C library tests
    install -m 0755 ${B}/tests/.libs/gpiod-test ${D}${PTEST_PATH}/tests/

    # Tools are always built so let's always install them for ptest even if
    # we're not selecting libgpiod-tools.
    for tool in ${FILES:${PN}-tools}; do
        install ${B}/tools/.libs/$(basename $tool) ${D}${PTEST_PATH}/tests/
    done

    if ${@bb.utils.contains('PACKAGECONFIG', 'cxx', 'true', 'false', d)}; then
        install -m 0755 ${B}/bindings/cxx/tests/.libs/gpiod-cxx-test ${D}${PTEST_PATH}/tests/
    fi
}

