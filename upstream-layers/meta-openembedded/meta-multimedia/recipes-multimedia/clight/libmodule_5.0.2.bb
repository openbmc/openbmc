SUMMARY = "Small and simple C actor library for modular projects"
HOMEPAGE = "https://github.com/FedeDP/libmodule"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f3c068505fd5a09e90662bfca90ad04"

SRCREV = "b373482a43482459b37bcf601e21846e9c9563d1"
SRC_URI = "git://github.com/FedeDP/${BPN};protocol=https;branch=release/5.0.x;tag=${PV}"

inherit cmake pkgconfig

FILES:${PN} += " \
    ${libdir}/* \
    ${datadir}/* \
"
