SUMMARY = "A networking benchmarking tool"
DESCRIPTION = "Network performance benchmark including tests for TCP, UDP, sockets, ATM and more."
SECTION = "net"
HOMEPAGE = "http://www.netperf.org/"
LICENSE = "netperf"
LICENSE_FLAGS = "non-commercial"


SRC_URI="ftp://ftp.netperf.org/netperf/archive/netperf-${PV}.tar.bz2 \
         file://cpu_set.patch \
         file://vfork.patch \
         file://init"
SRC_URI[md5sum] = "9654ffdfd4c4f2c93ce3733cd9ed9236"
SRC_URI[sha256sum] = "cd8dac710d4273d29f70e8dbd09353a6362ac58a11926e0822233c0cb230323a"

LIC_FILES_CHKSUM = "file://COPYING;md5=a0ab17253e7a3f318da85382c7d5d5d6"

inherit update-rc.d autotools

S = "${WORKDIR}/netperf-${PV}"

# cpu_set.patch plus _GNU_SOURCE makes src/netlib.c compile with CPU_ macros
CFLAGS_append = " -DDO_UNIX -DDO_IPV6 -D_GNU_SOURCE"

# set the "_FILE_OFFSET_BITS" preprocessor symbol to 64 to support files
# larger than 2GB
CFLAGS_append = "${@base_contains('DISTRO_FEATURES', 'largefile', \
    ' -D_FILE_OFFSET_BITS=64', '', d)}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[sctp] = "--enable-sctp,--disable-sctp,lksctp-tools,"

# autotools.bbclass attends to include m4 files with path depth <= 2 by
# "find ${S} -maxdepth 2 -name \*.m4", so move m4 files from m4/m4.
do_configure_prepend() {
    test -d ${S}/m4/m4 && mv -f ${S}/m4/m4 ${S}/m4-files
}

do_install() {
    sed -e 's#/usr/sbin/#${sbindir}/#g' -i ${WORKDIR}/init

    install -d ${D}${sbindir} ${D}${bindir} ${D}${sysconfdir}/init.d
    install -m 4755 src/netperf ${D}${bindir}
    install -m 4755 src/netserver ${D}${sbindir}
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/netperf

    # man
    install -d ${D}${mandir}/man1/
    install -m 0644 ${S}/doc/netserver.man ${D}${mandir}/man1/netserver.1
    install -m 0644 ${S}/doc/netperf.man ${D}${mandir}/man1/netperf.1

    # move scripts to examples directory
    install -d ${D}${docdir}/netperf/examples
    install -m 0644 ${S}/doc/examples/*_script ${D}${docdir}/netperf/examples/

    # docs ..
    install -m 0644 ${S}/COPYING ${D}${docdir}/netperf
    install -m 0644 ${S}/Release_Notes ${D}${docdir}/netperf
    install -m 0644 ${S}/README ${D}${docdir}/netperf
    install -m 0644 ${S}/doc/netperf_old.ps ${D}${docdir}/netperf
}

RRECOMMENDS_${PN} += "${@base_contains('PACKAGECONFIG', 'sctp', 'kernel-module-sctp', '', d)}"

INITSCRIPT_NAME="netperf"
INITSCRIPT_PARAMS="defaults"
