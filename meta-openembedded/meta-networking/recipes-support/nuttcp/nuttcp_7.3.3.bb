SUMMARY = "network performance measurement tool"
DESCRIPTION = "nuttcp is a network performance measurement tool intended for use by network \
and system managers. Its most basic usage is to determine the raw TCP (or UDP) \
network layer throughput by transferring memory buffers from a source system \
across an interconnecting network to a destination system, either transferring \
data for a specified time interval, or alternatively transferring a specified \
number of bytes."
HOMEPAGE = "http://www.nuttcp.net/Welcome%20Page.html"
LICENSE = "GPL-2.0"
SECTION = "net"
LIC_FILES_CHKSUM = "file://${BP}.c;beginline=4;endline=30;md5=c55daba5a5a869a123c3565de07d15a6"

SRC_URI = "http://nuttcp.net/${BPN}/beta/${BP}.c"
SRC_URI[md5sum] = "dfbff3c38fb0cbdc474ca6d13539d425"
SRC_URI[sha256sum] = "d68e291a72375d76f301d54aa945727c95e78090aa6783a4844764e632e98a4a"

S = "${WORKDIR}"

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -o nuttcp nuttcp-${PV}.c
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 nuttcp ${D}${bindir}
}
