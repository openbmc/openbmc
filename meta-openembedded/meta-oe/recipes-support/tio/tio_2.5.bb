SUMMARY = "tio - a simple serial device I/O tool"
DESCRIPTION = "tio is a simple serial device tool which features a \
    straightforward command-line and configuration file interface to easily \
    connect to serial TTY devices for basic I/O operations."

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e1a95b7892d3015ecd6d0016f601f2c"

SRC_URI = "git://github.com/tio/tio;protocol=https;nobranch=1"
SRCREV = "a2bbab068428275febeea13906654b9e58400c95"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS += " libinih"
RDEPENDS:${PN} += " libinih"

FILES:${PN} += " /usr/share/bash-completion/completions/tio "
