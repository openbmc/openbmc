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
LIC_FILES_CHKSUM = "file://COPYING;md5=626a5970304daa1fcb87f757fb42b795"

DEPENDS += "openssl"

PARALLEL_MAKE = ""

SRC_URI = "${SOURCEFORGE_MIRROR}/ipmiutil/ipmiutil-${PV}.tar.gz \
           file://fix_systemd_path.patch \
           file://0001-Prevent-access-times-from-changing-resulting-gzip-md.patch \
          "
SRC_URI[md5sum] = "292d6df25cad678bb27e5c8cdc6748f9"
SRC_URI[sha256sum] = "58ccdbd5755d7dd72478756715af09e9c73330dfad2b91dbf03d2ac504b301a3"

inherit autotools-brokensep pkgconfig systemd

PACKAGECONFIG ?= "lanplus gpl"
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
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

COMPATIBLE_HOST = '(x86_64|i.86).*-linux'
