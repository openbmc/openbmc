SUMMARY = "Tools for zmodem/xmodem/ymodem file transfer"
DESCRIPTION = "Lrzsz is a cosmetically modified zmodem/ymodem/xmodem package built from \
the public-domain version of Chuck Forsberg's rzsz package. \
These programs use error correcting protocols ({z,x,y}modem) to send (sz, sx, sb) and \
receive (rz, rx, rb) files over a dial-in serial port from a variety of programs \
running under various operating systems. "
HOMEPAGE = "http://www.ohse.de/uwe/software/lrzsz.html"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
			file://src/lrz.c;beginline=1;endline=10;md5=5276956373ff7d8758837f6399a1045f"
SECTION = "console/network"
DEPENDS = ""
PR = "r6"

SRC_URI = "http://www.ohse.de/uwe/releases/lrzsz-${PV}.tar.gz \
           file://autotools-update.patch \
	   file://autotools.patch \
	   file://makefile.patch \
           file://lrzsz-check-locale.h.patch \
           file://cve-2018-10195.patch \
           file://include.patch \
           "

SRC_URI[md5sum] = "b5ce6a74abc9b9eb2af94dffdfd372a4"
SRC_URI[sha256sum] = "c28b36b14bddb014d9e9c97c52459852f97bd405f89113f30bee45ed92728ff1"

UPSTREAM_CHECK_URI = "http://ohse.de/uwe/software/lrzsz.html"

inherit autotools gettext

do_install() {
	install -d ${D}${bindir}/
	install -m 0755 src/lrz src/lsz ${D}${bindir}/
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "rz rx rb sz sx sb"

ALTERNATIVE_TARGET[rz] = "${bindir}/lrz"
ALTERNATIVE_TARGET[rx] = "${bindir}/lrz"
ALTERNATIVE_TARGET[rb] = "${bindir}/lrz"

ALTERNATIVE_TARGET[sz] = "${bindir}/lsz"
ALTERNATIVE_TARGET[sx] = "${bindir}/lsz"
ALTERNATIVE_TARGET[sb] = "${bindir}/lsz"
