SUMMARY = "A networking benchmarking tool"
DESCRIPTION = "Network performance benchmark including tests for TCP, UDP, sockets, ATM and more."
SECTION = "net"
HOMEPAGE = "http://www.netperf.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e661ab33a2a71ad6652c178dedf8aaa2"

PV = "2.7.0+git"

SRC_URI = "git://github.com/HewlettPackard/netperf.git;branch=master;protocol=https \
           file://cpu_set.patch \
           file://vfork.patch \
           file://init \
           file://netserver.service \
           file://0001-netlib.c-Move-including-sched.h-out-og-function.patch \
           file://0001-nettest_omni-Remove-duplicate-variable-definitions.patch \
           file://netserver_permissions.patch \
           "

SRCREV = "3bc455b23f901dae377ca0a558e1e32aa56b31c4"

S = "${WORKDIR}/git"

inherit update-rc.d autotools texinfo systemd

# cpu_set.patch plus _GNU_SOURCE makes src/netlib.c compile with CPU_ macros
CFLAGS:append = " -DDO_UNIX -DDO_IPV6 -D_GNU_SOURCE"

# set the "_FILE_OFFSET_BITS" preprocessor symbol to 64 to support files
# larger than 2GB
CFLAGS:append = " -D_FILE_OFFSET_BITS=64"

PACKAGECONFIG ??= ""
PACKAGECONFIG[sctp] = "--enable-sctp,--disable-sctp,lksctp-tools,"
PACKAGECONFIG[intervals] = "--enable-intervals,--disable-intervals,,"
PACKAGECONFIG[histogram] = "--enable-histogram,--disable-histogram,,"

# autotools.bbclass attends to include m4 files with path depth <= 2 by
# "find ${S} -maxdepth 2 -name \*.m4", so move m4 files from m4/m4.
do_configure:prepend() {
    test -d ${S}/m4/m4 && mv -f ${S}/m4/m4 ${S}/m4-files
}

do_install() {
    sed -e 's#/usr/sbin/#${sbindir}/#g' -i ${UNPACKDIR}/init
    install -d ${D}${sbindir} ${D}${bindir} ${D}${sysconfdir}/init.d ${D}${systemd_system_unitdir}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        sed -e 's#/usr/sbin/#${sbindir}/#g' ${UNPACKDIR}/netserver.service > ${D}${systemd_system_unitdir}/netserver.service
    fi
    install -m 4755 src/netperf ${D}${bindir}
    install -m 4755 src/netserver ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/netperf

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

RRECOMMENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'sctp', 'kernel-module-sctp', '', d)}"

INITSCRIPT_NAME="netperf"
INITSCRIPT_PARAMS="defaults"
SYSTEMD_SERVICE:${PN} = "netserver.service"
