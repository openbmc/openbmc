
SUMMARY = "CANopenTerm is a versatile software tool to analyse and \
          configure CANopen devices. \
          "
DESCRIPTION = "CANopenTerm is an open-source software tool designed for the \
              development, testing, and analysis of CANopen CC networks and \
              devices.  It extends its capabilities to support other CAN CC \
              protocols, including SAE J1939 and OBD-II. \
              "
HOMEPAGE = "https://canopenterm.de"
BUGTRACKER = "https://github.com/CANopenTerm/CANopenTerm/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=10e84ea70e8c3a1fbc462f5424806474"

DEPENDS = "cjson libinih libsdl2 lua libsocketcan pocketpy"

SRC_URI = "git://github.com/CANopenTerm/CANopenTerm.git;protocol=https;branch=main"

SRCREV  = "e0760b2e9657907e691be4df384ca7617109635d"

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE += "-DBUILD_YOCTO=ON"

FILES:${PN} += "${bindir}/CANopenTerm ${bindir}/codb2json ${datadir}"

RDEPENDS:${PN} = "cjson libinih libsdl2 lua libsocketcan pocketpy"
