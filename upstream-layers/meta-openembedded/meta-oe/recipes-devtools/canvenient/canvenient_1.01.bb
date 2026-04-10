
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
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bd2edc721b4a0289efe949bdbe7dda79"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/CANopenTerm/CANvenient.git;protocol=https;branch=main;tag=v${PV}"

SRCREV  = "b8b37e3915caf5bce93f38c2c6cca71356dcfcab"

inherit cmake ptest


do_install:append() {
    install -d ${D}${includedir}
    install -d ${D}${libdir}

    install -m 0644 ${S}/include/CANvenient.h ${D}${includedir}/
    install -m 0755 ${B}/libCANvenient.so.1.0.1 ${D}${libdir}/

    ln -sf libCANvenient.so.1.0.1 ${D}${libdir}/libCANvenient.so.1
    ln -sf libCANvenient.so.1.0.1 ${D}${libdir}/libCANvenient.so
}

FILES:${PN} += "${libdir}/libCANvenient.so ${libdir}/libCANvenient.so.*"
FILES:${PN}-dev += "${includedir}"

SOLIBS = ".so"

RDEPENDS:${PN} = "libsocketcan"
