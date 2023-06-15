DESCRIPTION = "netkit-ft includes the ftp client."
SECTION = "net"
HOMEPAGE="ftp://ftp.uk.linux.org/pub/linux/Networking/netkit"
LICENSE = "BSD-4-Clause"

LIC_FILES_CHKSUM = "file://ftp/ftp.c;beginline=2;endline=3;md5=2d40a75a50d83b8f6317b3f53db72bfa"

SRC_URI = "${DEBIAN_MIRROR}/main/n/netkit-ftp/netkit-ftp_${PV}.orig.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/n/netkit-ftp/netkit-ftp_${PV}-34.debian.tar.xz;name=patch34 \
           file://Add_ARG_MAX_define.patch \
           file://0001-ftp-include-sys-types.h-for-u_long.patch \
           "
SRC_URI[archive.sha256sum] = "61c913299b81a4671ff089aac821329f7db9bc111aa812993dd585798b700349"
SRC_URI[patch34.sha256sum] = "716b984bc6926ed98345fa4e68adcee2efcf08d0f7315d6be8ad6de76f255748"

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
FILES:${PN} = "${bindir}/*"
FILES:${PN}-doc = "${mandir}"
FILES:${PN}-dbg = "${prefix}/src/debug \
            ${bindir}/.debug"

RDEPENDS:${PN} = "readline"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
