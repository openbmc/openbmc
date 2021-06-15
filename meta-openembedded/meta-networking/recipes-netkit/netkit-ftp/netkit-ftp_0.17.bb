DESCRIPTION = "netkit-ft includes the ftp client."
SECTION = "net"
HOMEPAGE="ftp://ftp.uk.linux.org/pub/linux/Networking/netkit"
LICENSE = "BSD-4-Clause"

LIC_FILES_CHKSUM = "file://ftp/ftp.c;beginline=2;endline=3;md5=2d40a75a50d83b8f6317b3f53db72bfa"

SRC_URI = "${DEBIAN_MIRROR}/main/n/netkit-ftp/netkit-ftp_${PV}.orig.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/n/netkit-ftp/netkit-ftp_${PV}-31.debian.tar.xz;name=patch31 \
           file://Add_ARG_MAX_define.patch \
           file://0001-ftp-include-sys-types.h-for-u_long.patch \
           "

SRC_URI[archive.md5sum] = "94441610c9b86ef45c4c6ec609444060"
SRC_URI[archive.sha256sum] = "61c913299b81a4671ff089aac821329f7db9bc111aa812993dd585798b700349"
SRC_URI[patch31.md5sum] = "93d71e28ce70df69e080c7f90da63cac"
SRC_URI[patch31.sha256sum] = "4edd46a32d70daa7ba00f0ebf0118dc5d17dff23d6e46aa21a2722be2e22d1c1"

inherit autotools-brokensep update-alternatives

CLEANBROKEN = "1"

do_configure () {
    ./configure --prefix=${prefix}
    echo "LDFLAGS=${LDFLAGS}" >> MCONFIG
}

BINMODE = "0755"
MANMODE = "0644"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man5

    install -m${BINMODE} ${S}/ftp/ftp ${D}${bindir}
    ln -sf ftp ${D}${bindir}/pftp
    install -m${MANMODE} ${S}/ftp/ftp.1 ${D}${mandir}/man1
    ln -sf ftp.1 ${D}${mandir}/man1/pftp.1
    install -m${MANMODE} ${S}/ftp/netrc.5 ${D}${mandir}/man5
}

PACKAGES = "${PN} ${PN}-doc ${BPN}-dbg"
FILES_${PN} = "${bindir}/*"
FILES_${PN}-doc = "${mandir}"
FILES_${PN}-dbg = "${prefix}/src/debug \
            ${bindir}/.debug"

RDEPENDS_${PN} = "readline"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
