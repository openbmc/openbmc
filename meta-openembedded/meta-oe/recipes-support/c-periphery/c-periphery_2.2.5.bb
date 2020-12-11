SUMMARY = "C-Periphery lib used to access GPIO, LED, PWM, SPI, I2C, MMIO, Serial"
DESCRIPTION = "A C library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux"

HOMEPAGE = "https://github.com/vsergeev/c-periphery"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4097ec544cf01e9c7cfc4bdf8e4ed887"

SRC_URI = "git://github.com/vsergeev/c-periphery;protocol=https"
SRCREV = "513f5edbcf2f2ec5c0cd711d1cc312eb2d753f1b"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

BBCLASSEXTEND = "native nativesdk"

