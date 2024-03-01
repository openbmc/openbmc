SUMMARY = "Library for interfacing with common SoC peripherals"
DESCRIPTION = "libsoc is a C library to interface with common peripherals (gpio, i2c, spi, pwm) \
               found in SoC (System on Chips) through generic Linux Kernel interfaces."

HOMEPAGE = "https://github.com/jackmitch/libsoc"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENCE;md5=e0bfebea12a718922225ba987b2126a5"

inherit autotools pkgconfig python3-dir

SRCREV = "fd1ad6e7823fa76d8db0d3c5884faffa8ffddafb"
SRC_URI = "git://github.com/jackmitch/libsoc.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

BOARD ??= "devboard"

PACKAGECONFIG ?= ""

PACKAGECONFIG[disabledebug] = "--disable-debug,,"
PACKAGECONFIG[allboardconfigs] = "--with-board-configs,,"
PACKAGECONFIG[enableboardconfig] = "--enable-board=${BOARD},,"
PACKAGECONFIG[python] = "--enable-python=${STAGING_BINDIR_NATIVE}/python3-native/python3,,python3 python3-native"

PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'python', \
    'python3-libsoc-staticdev python3-libsoc', '', d)}"

RDEPENDS:${PN} = "libgcc"

FILES:python3-libsoc-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*/*.a"
FILES:python3-libsoc += "${PYTHON_SITEPACKAGES_DIR}"
