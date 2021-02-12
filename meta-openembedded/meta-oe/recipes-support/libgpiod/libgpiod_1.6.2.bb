SUMMARY = "C library and tools for interacting with the linux GPIO character device"
AUTHOR = "Bartosz Golaszewski <bgolaszewski@baylibre.com>"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = "https://www.kernel.org/pub/software/libs/${BPN}/${BP}.tar.xz \
           file://run-ptest \
"

SRC_URI[md5sum] = "4765470becb619fead3cdaeac61b9a77"
SRC_URI[sha256sum] = "c601e71846f5ab140c83bc757fdd62a4fda24a9cee39cc5e99c96ec2bf1b06a9"

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

PACKAGES =+ "${PN}-tools libgpiodcxx"
FILES_${PN}-tools = "${bindir}/*"
FILES_libgpiodcxx = "${libdir}/libgpiodcxx.so.*"

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN}-python += "${RRECOMMENDS_PYTHON}"

PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'tests', '', d)}"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp ${B}/tests/.libs/gpiod-test ${D}${PTEST_PATH}/tests/
}
