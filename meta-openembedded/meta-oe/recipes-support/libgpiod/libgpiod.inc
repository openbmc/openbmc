SUMMARY = "C library and tools for interacting with the linux GPIO character device"
AUTHOR = "Bartosz Golaszewski <brgl@bgdev.pl>"

require libgpiod-src.inc

inherit autotools pkgconfig ptest

SRC_URI += "file://run-ptest"

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
FILES:${PN}-ptest += " \
    ${bindir}/gpiod-test \
    ${bindir}/gpio-tools-test \
    ${bindir}/gpio-tools-test.bats \
    ${bindir}/gpiod-cxx-test \
"
FILES:libgpiodcxx = "${libdir}/libgpiodcxx.so.*"

RRECOMMENDS:${PN}-ptest += "coreutils"
RDEPENDS:${PN}-ptest += "${@bb.utils.contains('PTEST_ENABLED', '1', 'bats', '', d)}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/

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
}

