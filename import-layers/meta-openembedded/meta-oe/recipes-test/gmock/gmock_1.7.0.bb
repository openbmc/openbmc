DESCRIPTION = "Google C++ Mocking Framework"
SECTION = "libs"
HOMEPAGE = "http://code.google.com/p/googlemock/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

SRC_URI = "\
    http://googlemock.googlecode.com/files/${BPN}-${PV}.zip \
    file://cmake-Add-install-command-for-libraries-and-headers.patch \
    file://cmake-gmock.pc.in-Add-pkg-config-support.patch \
"

SRC_URI[md5sum] = "073b984d8798ea1594f5e44d85b20d66"
SRC_URI[sha256sum] = "26fcbb5925b74ad5fc8c26b0495dfc96353f4d553492eb97e85a8a6d2f43095b"

inherit lib_package cmake

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

RDEPENDS_${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"
