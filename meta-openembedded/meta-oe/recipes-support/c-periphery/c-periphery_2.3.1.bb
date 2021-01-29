SUMMARY = "C-Periphery lib used to access GPIO, LED, PWM, SPI, I2C, MMIO, Serial"
DESCRIPTION = "A C library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux"

HOMEPAGE = "https://github.com/vsergeev/c-periphery"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4097ec544cf01e9c7cfc4bdf8e4ed887"

SRC_URI = "git://github.com/vsergeev/c-periphery;protocol=https"
SRCREV = "23bfa4ab481edbad82a69ee385fc58ce03b63084"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

BBCLASSEXTEND = "native nativesdk"

