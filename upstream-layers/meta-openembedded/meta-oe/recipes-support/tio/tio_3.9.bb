SUMMARY = "tio - a simple serial device I/O tool"
DESCRIPTION = "tio is a simple serial device tool which features a \
    straightforward command-line and configuration file interface to easily \
    connect to serial TTY devices for basic I/O operations."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d25b0ab86d83b943da4d25251a2c21d7"
DEPENDS += " glib-2.0 lua"
SRCREV = "bdfe87e1cbf6e3bfd48324a25ea026fcd3cc47e9"

SRC_URI = "git://github.com/tio/tio;protocol=https;branch=master;tag=v${PV}"


inherit meson pkgconfig

RDEPENDS:${PN} += " lua"

FILES:${PN} += " ${datadir}/bash-completion/completions/tio"
