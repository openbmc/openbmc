SUMMARY = "Easy progress reporting for Python"
HOMEPAGE = "http://github.com/verigak/progress/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aef5566ac4fede9815eccf124c281317"

SRC_URI[md5sum] = "f65ccfcc2339d522b2dd5801aaa40dc7"
SRC_URI[sha256sum] = "c88d89ee3bd06716a0b8b5504d9c3bcb3c1c0ab98f96dc7f1dc5f56812a4f60a"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
"

BBCLASSEXTEND = "native nativesdk"
