SUMMARY = "An Enhanced Printer Spooler"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only | Artistic-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6570d8d699af1883db9d0e733ac9bfb"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "c92597671f4c7cbe8bb3f38fbc4283354db84c6abff1efb675fa2e120421915d"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/lprng/files/lprng/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext

EXTRA_OECONF = "--disable-ssl --disable-kerberos --enable-force_localhost \
				CHOWN=${base_bindir}/chown CHGRP=${base_bindir}/chgrp \
				OPENSSL=${bindir}/openssl PRUTIL=${bindir}/pr"
FILES:${PN}-dbg += "${libdir}/lprng/filters/.debug"

# configure: WARNING: Program 'clear' is not found. Set environment CLEAR=no if you do not want to use it
export CLEAR = "no"

do_install:append() {
    mv ${D}/etc/printcap.sample ${D}/etc/printcap
    mv ${D}/etc/lpd/lpd.conf.sample ${D}/etc/lpd/lpd.conf
    mv ${D}/etc/lpd/lpd.perms.sample ${D}/etc/lpd/lpd.perms
}
