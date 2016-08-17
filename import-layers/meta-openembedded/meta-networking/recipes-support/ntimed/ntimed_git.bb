SUMMARY = "Network time synchronization software, NTPD replacement"
DESCRIPTION = "This is a preview/early-access/alpha/buzzword-of-the-times \
release of a new FOSS project written to gradually take over the world of \
networked timekeeping."
HOMEPAGE = "https://github.com/bsdphk/Ntimed"
SECTION = "net"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://main.c;startline=2;endline=24;md5=eda11d21005319bb76cbb6f911f0f66d"

SRC_URI = "git://github.com/bsdphk/Ntimed \
           file://use-ldflags.patch"

PV = "0.0+git${SRCPV}"
SRCREV = "db0abbb4c80f2ecef6bc5d9639bca5bea28532a2"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

do_configure () {
    sh ./configure
}

do_compile () {
    oe_runmake
}

do_install () {
    install -D -m 0755 ntimed-client ${D}${sbindir}/ntimed-client
}

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "ntimed-client"

PACKAGE_BEFORE_PN += "ntimed-client"
FILES_ntimed-client = "${sbindir}/ntimed-client"
