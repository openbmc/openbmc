SUMMARY = "Tools for zmodem/xmodem/ymodem file transfer"
DESCRIPTION = "Lrzsz is a cosmetically modified zmodem/ymodem/xmodem package built from \
the public-domain version of Chuck Forsberg's rzsz package. \
These programs use error correcting protocols ({z,x,y}modem) to send (sz, sx, sb) and \
receive (rz, rx, rb) files over a dial-in serial port from a variety of programs \
running under various operating systems. "
HOMEPAGE = "http://www.ohse.de/uwe/software/lrzsz.html"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
			file://src/lrz.c;beginline=1;endline=10;md5=5276956373ff7d8758837f6399a1045f"
SECTION = "console/network"
DEPENDS = ""

SRC_URI = "https://www.ohse.de/uwe/releases/lrzsz-${PV}.tar.gz \
           file://0001-Fix-build-with-32-bit-targets-under-Werror.patch \
           file://0002-Remove-unused-file-scope-variables.patch \
           "
SRC_URI[sha256sum] = "112f91059fbd118e016d800c1a5c5a1702e965d8b9bfbda6015e44e20bfb93c9"

UPSTREAM_CHECK_URI = "http://ohse.de/uwe/software/lrzsz.html"

EXTRA_OECONF = "--disable-Werror"

inherit autotools gettext

do_install() {
	install -d ${D}${bindir}/
	install -m 0755 src/lrz src/lsz ${D}${bindir}/
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "rz rx rb sz sx sb"

ALTERNATIVE_TARGET[rz] = "${bindir}/lrz"
ALTERNATIVE_TARGET[rx] = "${bindir}/lrz"
ALTERNATIVE_TARGET[rb] = "${bindir}/lrz"

ALTERNATIVE_TARGET[sz] = "${bindir}/lsz"
ALTERNATIVE_TARGET[sx] = "${bindir}/lsz"
ALTERNATIVE_TARGET[sb] = "${bindir}/lsz"
