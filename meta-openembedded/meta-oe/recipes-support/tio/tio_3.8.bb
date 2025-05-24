SUMMARY = "tio - a simple serial device I/O tool"
DESCRIPTION = "tio is a simple serial device tool which features a \
    straightforward command-line and configuration file interface to easily \
    connect to serial TTY devices for basic I/O operations."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e1a95b7892d3015ecd6d0016f601f2c"
DEPENDS += " glib-2.0 lua"
SRCREV = "01e637cdf4d2d781a87a2fa68e49e7f8fccd0552"

SRC_URI = "git://github.com/tio/tio;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit meson pkgconfig

RDEPENDS:${PN} += " lua"

FILES:${PN} += " ${datadir}/bash-completion/completions/tio"
