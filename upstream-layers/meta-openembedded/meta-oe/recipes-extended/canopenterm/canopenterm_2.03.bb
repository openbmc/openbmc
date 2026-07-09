
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
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8977ecf3af54da2a5271158cc969bd45"

DEPENDS = "canvenient cjson isocline libinih libsdl3 lua pocketpy"

SRC_URI = "git://github.com/CANopenTerm/CANopenTerm.git;protocol=https;branch=main;tag=v${PV}"
SRCREV  = "63ccae8262f5e796ffca0c8a44fdf7ff0bd28571"


inherit cmake ptest

EXTRA_OECMAKE += "-DUSE_SYSTEM_LIBS=ON"

FILES:${PN} += "${bindir}/CANopenTerm ${bindir}/codb2json ${datadir}"

RDEPENDS:${PN} = "canvenient cjson libinih libsdl3 lua pocketpy"
