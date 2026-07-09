
SUMMARY = "CANvenient is an abstraction layer for multiple CAN APIs \
          on Windows and Linux. \
          "
DESCRIPTION = "CANvenient is an abstraction layer for multiple CAN APIs on \
              Windows and Linux. It provides a unified interface for CAN \
              communication, allowing developers to write code that is \
              portable across different platforms and CAN hardware. \
              "
HOMEPAGE = "https://canopenterm.de/canvenient"
BUGTRACKER = "https://github.com/CANopenTerm/CANvenient/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8977ecf3af54da2a5271158cc969bd45"

DEPENDS = "libsocketcan"

PV .= "+git"

SRC_URI = "git://github.com/CANopenTerm/CANvenient.git;protocol=https;branch=main"

SRCREV  = "23408935f230f59632eafd56056824f8e91cd896"

inherit cmake ptest


do_install:append() {
    install -d ${D}${includedir}
    install -d ${D}${libdir}

    install -m 0644 ${S}/include/CANvenient.h ${D}${includedir}/
    install -m 0755 ${B}/libCANvenient.so.1.0.2 ${D}${libdir}/

    ln -sf libCANvenient.so.1.0.2 ${D}${libdir}/libCANvenient.so.1
    ln -sf libCANvenient.so.1.0.2 ${D}${libdir}/libCANvenient.so
}

FILES:${PN} += "${libdir}/libCANvenient.so ${libdir}/libCANvenient.so.*"
FILES:${PN}-dev += "${includedir}"

SOLIBS = ".so"

RDEPENDS:${PN} = "libsocketcan"
