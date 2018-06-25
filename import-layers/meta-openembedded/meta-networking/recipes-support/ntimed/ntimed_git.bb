SUMMARY = "Network time synchronization software, NTPD replacement"
DESCRIPTION = "This is a preview/early-access/alpha/buzzword-of-the-times \
release of a new FOSS project written to gradually take over the world of \
networked timekeeping."
HOMEPAGE = "https://github.com/bsdphk/Ntimed"
SECTION = "net"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://main.c;beginline=2;endline=24;md5=89db8e76f2951f3fad167e7aa9718a44"

SRC_URI = "git://github.com/bsdphk/Ntimed \
           file://use-ldflags.patch"

PV = "0.0+git${SRCPV}"
SRCREV = "db0abbb4c80f2ecef6bc5d9639bca5bea28532a2"

S = "${WORKDIR}/git"

# use adjtimex on musl
CFLAGS_append_libc-musl = " -Dntp_adjtime=adjtimex"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

do_configure () {
    sh ${S}/configure
}

do_install () {
    install -D -m 0755 ntimed-client ${D}${sbindir}/ntimed-client
}

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "ntimed-client"

PACKAGE_BEFORE_PN += "ntimed-client"
FILES_ntimed-client = "${sbindir}/ntimed-client"
