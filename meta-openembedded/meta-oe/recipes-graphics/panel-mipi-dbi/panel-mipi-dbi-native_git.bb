SUMMARY = "Firmware file compiler for the panel-mipi-dbi display driver"
DESCRIPTION = "The panel-mipi-dbi Linux display driver allows using the same \
               driver for most MIPI DBI based display panels. \
               This means many displays attached via SPI, even if the \
               datasheet does not explicitly mention DBI support. \
               To do so it uses tiny firmware files that contain \
               display/controller-specific initialization commands. \
               The mipi-dbi-cmd tool compiles these firmware files from a \
               text format to a firmware blob format."
HOMEPAGE = "https://github.com/notro/panel-mipi-dbi"
SECTION = "graphics"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://mipi-dbi-cmd;beginline=4;endline=13;md5=5e3d3f14cc87aa9e8976d728520cbcae"
SRCREV = "1cbd40135a8c7f25d7b444a7fac77fd3c3ad471e"

SRC_URI = "git://github.com/notro/panel-mipi-dbi.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit native

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install () {
    install -D -p -m 0755 ${S}/mipi-dbi-cmd ${D}${bindir}/mipi-dbi-cmd
}

RDEPENDS:${PN} += "python3-native"
