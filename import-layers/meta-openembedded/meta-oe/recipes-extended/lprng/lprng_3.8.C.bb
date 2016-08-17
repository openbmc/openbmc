SUMMARY = "An Enhanced Printer Spooler"
SECTION = "console/utils"
LICENSE = "GPLv2 | Artistic-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6570d8d699af1883db9d0e733ac9bfb"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "5901bed95e61d2bea3ba3056056af432"
SRC_URI[sha256sum] = "694a1747a96385b89e93f43343bf35cee5c8c73353a83814106911c99f09de10"

inherit autotools gettext

EXTRA_OECONF = "--disable-ssl --disable-kerberos --enable-force_localhost"
FILES_${PN}-dbg += "${libdir}/lprng/filters/.debug"

do_install_append() {
    mv ${D}/etc/printcap.sample ${D}/etc/printcap
    mv ${D}/etc/lpd/lpd.conf.sample ${D}/etc/lpd/lpd.conf
    mv ${D}/etc/lpd/lpd.perms.sample ${D}/etc/lpd/lpd.perms
}
