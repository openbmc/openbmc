SUMMARY = "Interface for user-level network packet capture"
DESCRIPTION = "Libpcap provides a portable framework for low-level network \
monitoring.  Libpcap can provide network statistics collection, \
security monitoring and network debugging."
HOMEPAGE = "http://www.tcpdump.org/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=53067&atid=469577"
SECTION = "libs/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5eb289217c160e2920d2e35bddc36453 \
                    file://pcap.h;beginline=1;endline=32;md5=39af3510e011f34b8872f120b1dc31d2"
DEPENDS = "flex-native bison-native"

SRC_URI = "https://www.tcpdump.org/release/${BP}.tar.gz"
SRC_URI[sha256sum] = "ed285f4accaf05344f90975757b3dbfe772ba41d1c401c2648b7fa45b711bdd4"

inherit autotools binconfig-disabled pkgconfig

BINCONFIG = "${bindir}/pcap-config"

# Explicitly disable dag support. We don't have recipe for it and if enabled here,
# configure script poisons the include dirs with /usr/local/include even when the
# support hasn't been detected. Do the same thing for DPDK.
EXTRA_OECONF = " \
                 --with-pcap=linux \
                 --without-dag \
                 --without-dpdk \
                 "
EXTRA_AUTORECONF += "--exclude=aclocal"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[bluez5] = "--enable-bluetooth,--disable-bluetooth,bluez5"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[libnl] = "--with-libnl,--without-libnl,libnl"

do_configure:prepend () {
    #remove hardcoded references to /usr/include
    sed 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i ${S}/configure.ac
}

BBCLASSEXTEND = "native"
