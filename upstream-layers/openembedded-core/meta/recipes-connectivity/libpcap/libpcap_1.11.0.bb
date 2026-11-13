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

SRC_URI = "https://www.tcpdump.org/release/${BP}.tar.xz \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "9237f5bae9dcf3a91823d9963ec43b7c0e2e3374ef2ad57d92c8cd39530f4723"

inherit autotools binconfig-disabled pkgconfig ptest

BINCONFIG = "${bindir}/pcap-config"

# Explicitly disable dag support. We don't have recipe for it and if enabled here,
# configure script poisons the include dirs with /usr/local/include even when the
# support hasn't been detected.
EXTRA_OECONF = " \
                 --with-pcap=linux \
                 --without-dag \
                 "

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5', '', d)} \
"
PACKAGECONFIG[bluez5] = "--enable-bluetooth,--disable-bluetooth,bluez5"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[libnl] = "--with-libnl,--without-libnl,libnl"

do_configure:prepend () {
    #remove hardcoded references to /usr/include
    sed 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i ${S}/configure.ac
}

do_compile_ptest() {
    oe_runmake -C ${B}/testprogs CFLAGS="${CFLAGS} ${LDFLAGS}" \
        filtertest translatetest enumeratetest
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/testprogs
    install -d ${D}${PTEST_PATH}/tests

    # Perl harness and its helper modules.
    install -m 0755 ${S}/testprogs/TESTrun ${D}${PTEST_PATH}/testprogs/
    install -m 0644 ${S}/testprogs/TESTlib.pm ${D}${PTEST_PATH}/testprogs/
    install -m 0644 ${S}/testprogs/TESTst.pm ${D}${PTEST_PATH}/testprogs/
    install -m 0644 ${S}/testprogs/TESTmt.pm ${D}${PTEST_PATH}/testprogs/

    # Offline test programs driven by the harness.
    install -m 0755 ${B}/testprogs/filtertest ${D}${PTEST_PATH}/testprogs/
    install -m 0755 ${B}/testprogs/translatetest ${D}${PTEST_PATH}/testprogs/
    install -m 0755 ${B}/testprogs/enumeratetest ${D}${PTEST_PATH}/testprogs/

    # Savefiles used by the filter tests (SAVEFILE_DIR is testprogs/../tests).
    cp -r ${S}/tests/* ${D}${PTEST_PATH}/tests/

    # config.h is read by TESTrun for feature-based test skipping.
    install -m 0644 ${B}/config.h ${D}${PTEST_PATH}/config.h
}

RDEPENDS:${PN}-ptest += "perl perl-modules coreutils diffutils"

BBCLASSEXTEND = "native nativesdk"
