SUMMARY = "C library and tools for interacting with the linux GPIO character device"
AUTHOR = "Bartosz Golaszewski <bgolaszewski@baylibre.com>"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = "https://www.kernel.org/pub/software/libs/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "064c2627397e6641c52de09a26951112"
SRC_URI[sha256sum] = "f1cda2789e6a13a92aefc012a76e5a7cc57a1b402d66f71df8719ee314b67699"

inherit autotools pkgconfig python3native

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"
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
