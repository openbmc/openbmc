
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
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=5f8a62fabd50ce3f1d7794bc849ae7a5"

DEPENDS = "cjson libinih libsdl3 lua libsocketcan pocketpy"

SRC_URI = "git://github.com/CANopenTerm/CANopenTerm.git;protocol=https;branch=main"

SRCREV  = "b0555360e5e8b444a2a9e14088fd253412184eb8"


inherit cmake ptest

EXTRA_OECMAKE += "-DBUILD_YOCTO=ON"

FILES:${PN} += "${bindir}/CANopenTerm ${bindir}/codb2json ${datadir}"

RDEPENDS:${PN} = "cjson libinih libsdl3 lua libsocketcan pocketpy"
