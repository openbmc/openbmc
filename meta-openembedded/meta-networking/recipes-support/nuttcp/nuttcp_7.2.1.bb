# Copyright (C) 2013 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

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
LIC_FILES_CHKSUM = "file://${BP}.c;beginline=4;endline=30;md5=ae7045c3c3616092e07d87f04ba0d960"

SRC_URI = "http://nuttcp.net/${BPN}/beta/${BP}.c"
SRC_URI[md5sum] = "1ebf4a08bad2a295a8155f02995e8754"
SRC_URI[sha256sum] = "c6e33810ccce67260f8d5d627f60e429d44f532365c58ed5673d035e2a59c4db"

S = "${WORKDIR}"

do_compile () {
    ${CC} ${CFLAGS} ${LDFLAGS} -o nuttcp nuttcp-${PV}.c
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 nuttcp ${D}${bindir}
}
