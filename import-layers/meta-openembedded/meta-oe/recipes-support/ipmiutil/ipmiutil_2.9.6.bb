SUMMARY = "ipmiutil is an easy-to-use set of IPMI server management utilities.\
It can get/set sensor thresholds, automate SEL management, do SOL console, etc."

DESCRIPTION = "The IPMI Management Utilities currently work with platforms that \
support the IPMI 1.5 or 2.0 specification.   IPMI servers can be managed\
locally, or remotely via IPMI LAN, even when the OS or main CPU is not\
functional.\n \
The ipmiutil utilities will use an IPMI Driver, either the Intel IPMI package \
(ipmidrvr, /dev/imb), MontaVista OpenIPMI (/dev/ipmi0), the valinux IPMI \
Driver (/dev/ipmikcs), or the LANDesk ldipmi daemon.  The ipmiutil utilities \
can also use direct user-space I/Os in Linux or FreeBSD if no IPMI driver \
is detected."

HOMEPAGE = "http://ipmiutil.sourceforge.net"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f9372493401f309e6149dd2ce0a044b"

#PARALLEL_MAKE = ""

SRC_URI = "${SOURCEFORGE_MIRROR}/ipmiutil/ipmiutil-${PV}.tar.gz \
    file://fix_systemd_path.patch "

SRC_URI[md5sum] = "462087995f05fa9e692ed7f55c840f71"
SRC_URI[sha256sum] = "884c1f3d8bfb0b33c303973d286c3166f5a537976451a0312e3524af54771519"

inherit autotools-brokensep pkgconfig systemd

PACKAGECONFIG ?= "lanplus gpl"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PACKAGECONFIG[systemd] = "--enable-systemd=${datadir}/${BPN}, --enable-systemd=no"
PACKAGECONFIG[sha256] = "--enable-sha256, --enable-sha256=no, openssl-native, openssl"
PACKAGECONFIG[lanplus] = "--enable-lanplus, --enable-lanplus=no, openssl-native, openssl"
PACKAGECONFIG[landesk] = "--enable-landesk, --enable-landesk=no"
PACKAGECONFIG[sensors] = "--enable-libsensors, --enable-libsensors=no" 

#build with some GPL code
PACKAGECONFIG[gpl] = "--enable-gpl, --enable-gpl=no"
#no GPL or LanPlus libs
PACKAGECONFIG[standalone] = "--enable-standalone, --enable-standalone=no"

CFLAGS += "-I${STAGING_INCDIR}"
LDFLAGS += "-L${STAGING_LIBDIR}"

do_configure () {
    aclocal
    libtoolize --automake --copy --force
    autoheader
    automake --foreign --add-missing --copy

    aclocal
    autoconf
    automake --foreign
    ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

COMPATIBLE_HOST = '(x86_64|i.86).*-linux'
