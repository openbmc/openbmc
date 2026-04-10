
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
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bd2edc721b4a0289efe949bdbe7dda79"

DEPENDS = "canvenient cjson isocline libinih libsdl3 lua pocketpy"

SRC_URI = "git://github.com/CANopenTerm/CANopenTerm.git;protocol=https;branch=main;tag=v${PV}"
SRCREV  = "4196097f71c7e59b27e9d8ac3c6cf58f44fa9f19"


inherit cmake ptest

EXTRA_OECMAKE += "-DBUILD_YOCTO=ON"

FILES:${PN} += "${bindir}/CANopenTerm ${bindir}/codb2json ${datadir}"

RDEPENDS:${PN} = "canvenient cjson libinih libsdl3 lua pocketpy"
