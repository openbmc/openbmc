SUMMARY = "Tool for controlling the Linux TUN/TAP driver"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://tunctl.c;beginline=1;endline=4;md5=ff3a09996bc5fff6bc5d4e0b4c28f999"

SRC_URI = "${SOURCEFORGE_MIRROR}/tunctl/tunctl-${PV}.tar.gz"

do_compile() {
    oe_runmake tunctl
}

do_install() {
    install -d ${D}/${sbindir}
    install -m 755 tunctl ${D}/${sbindir}
}
