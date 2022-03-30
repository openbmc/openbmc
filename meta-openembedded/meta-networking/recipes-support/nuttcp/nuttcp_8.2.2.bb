SUMMARY = "network performance measurement tool"
DESCRIPTION = "nuttcp is a network performance measurement tool intended for use by network \
and system managers. Its most basic usage is to determine the raw TCP (or UDP) \
network layer throughput by transferring memory buffers from a source system \
across an interconnecting network to a destination system, either transferring \
data for a specified time interval, or alternatively transferring a specified \
number of bytes."
HOMEPAGE = "http://www.nuttcp.net/Welcome%20Page.html"
LICENSE = "GPL-2.0-only"
SECTION = "net"
LIC_FILES_CHKSUM = "file://${BP}.c;beginline=4;endline=30;md5=496a7c0bb83c07ff528d226bf85e05c5"

UPSTREAM_CHECK_URI = "https://www.nuttcp.net/nuttcp/beta/"

SRC_URI = "http://nuttcp.net/${BPN}/beta/${BP}.c \
           file://nuttcp@.service \
           file://nuttcp.socket"
SRC_URI[md5sum] = "d3c92c4d2f261221193c3726c1b9a42f"
SRC_URI[sha256sum] = "8c5595bcd27c2fd66831be74c390df078cfb1870aa427f2511ac2586d236c8a1"

S = "${WORKDIR}"

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -o nuttcp nuttcp-${PV}.c
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${systemd_system_unitdir}
    install -m 0755 nuttcp ${D}${bindir}
    install -m 0644 ${WORKDIR}/nuttcp@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/nuttcp.socket ${D}${systemd_system_unitdir}
}

FILES:${PN} += " \
    ${bindir} \
    ${systemd_system_unitdir} \
"
