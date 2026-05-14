
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

PV .= "+git"

SRC_URI = "git://github.com/CANopenTerm/CANopenTerm.git;protocol=https;branch=main"
SRCREV  = "7a9466029a6e88345e65e794ff1779f778c9358a"


inherit cmake ptest

EXTRA_OECMAKE += "-DUSE_SYSTEM_LIBS=ON"

FILES:${PN} += "${bindir}/CANopenTerm ${bindir}/codb2json ${datadir}"

RDEPENDS:${PN} = "canvenient cjson libinih libsdl3 lua pocketpy"
