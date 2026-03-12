SUMMARY = "C-Periphery lib used to access GPIO, LED, PWM, SPI, I2C, MMIO, Serial"
DESCRIPTION = "A C library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux"

HOMEPAGE = "https://github.com/vsergeev/c-periphery"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88456d0a60de4c71ff4d787c212f253"

SRC_URI = "git://github.com/vsergeev/c-periphery;protocol=https;branch=master;tag=v${PV}"
SRCREV = "76c4edd4b5c43a597fd37c618ed32dbc2a27ec40"


inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

BBCLASSEXTEND = "native nativesdk"

