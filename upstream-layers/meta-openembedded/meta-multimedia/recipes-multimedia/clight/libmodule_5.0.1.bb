SUMMARY = "Small and simple C actor library for modular projects"
HOMEPAGE = "https://github.com/FedeDP/libmodule"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f3c068505fd5a09e90662bfca90ad04"

SRCREV = "3f60063e98631ce3fd25f70428b67ef15025597f"
SRC_URI = "git://github.com/FedeDP/${BPN};protocol=https;branch=master;tag=${PV} \
    file://0001-Update-cmake_minimum_required-to-3.5.patch \
"

inherit cmake pkgconfig

FILES:${PN} += " \
    ${libdir}/* \
    ${datadir}/* \
"
