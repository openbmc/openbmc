SUMMARY = "Interface for user-level network packet capture"
DESCRIPTION = "Libpcap provides a portable framework for low-level network \
monitoring.  Libpcap can provide network statistics collection, \
security monitoring and network debugging."
HOMEPAGE = "http://www.tcpdump.org/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=53067&atid=469577"
SECTION = "libs/network"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb289217c160e2920d2e35bddc36453 \
                    file://pcap.h;beginline=1;endline=32;md5=39af3510e011f34b8872f120b1dc31d2"
DEPENDS = "flex-native bison-native"

INC_PR = "r5"

SRC_URI = "http://www.tcpdump.org/release/${BP}.tar.gz"

BINCONFIG = "${bindir}/pcap-config"

inherit autotools binconfig-disabled pkgconfig bluetooth

EXTRA_OECONF = "--with-pcap=linux"
EXTRA_AUTORECONF += "--exclude=aclocal"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[bluez4] = "--enable-bluetooth,--disable-bluetooth,bluez4"
# Add a dummy PACKAGECONFIG for bluez5 since it is not supported by libpcap.
PACKAGECONFIG[bluez5] = ",,"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[libnl] = "--with-libnl,--without-libnl,libnl"

CPPFLAGS_prepend = "-I${S} "
CFLAGS_prepend = "-I${S} "
CXXFLAGS_prepend = "-I${S} "

do_configure_prepend () {
    sed -i -e's,^V_RPATH_OPT=.*$,V_RPATH_OPT=,' ${S}/pcap-config.in
}

BBCLASSEXTEND = "native"
