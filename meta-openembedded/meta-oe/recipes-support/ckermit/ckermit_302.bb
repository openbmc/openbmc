DESCRIPTION = "C-Kermit is a combined serial and network communication \
software package offering a consistent, medium-independent, \
cross-platform approach to connection establishment, terminal \
sessions, file transfer, character-set translation, and automation \
of communication tasks."
HOMEPAGE = "www.kermitproject.org/ck90.html"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.TXT;md5=932ca542d6c6cb8a59a0bcd76ab67cc3"

SRC_URI = "http://www.kermitproject.org/ftp/kermit/archives/cku${PV}.tar.gz;subdir=${BPN}-${PV} \
           file://0001-Fix-function-prototype-errors.patch \
           "
SRC_URI[md5sum] = "eac4dbf18b45775e4cdee5a7c74762b0"
SRC_URI[sha256sum] = "0d5f2cd12bdab9401b4c836854ebbf241675051875557783c332a6a40dac0711"


export CC2 = "${CC}"
export BINDIR = "${bindir}"
export MANDIR = "${mandir}/man1"
export INFODIR = "${infodir}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile () {
    # The original makefile doesn't differentiate between CC and CC_FOR_BUILD,
    # so we build wart manually. Note that you need a ckwart.o with the proper
    # timestamp to make this hack work:
    ${BUILD_CC} -c ckwart.c
    ${BUILD_CC} -o wart ckwart.o
    ./wart ckcpro.w ckcpro.c

    # read ${S}/ckccfg.txt to understand this :-)
    oe_runmake wermit CFLAGS="${CFLAGS} -DLINUX -DCK_POSIX_SIG \
        -DNOTCPOPTS -DLINUXFSSTND -DNOCOTFMC -DPOSIX -DUSE_STRERROR \
        -DNOSYSLOG -DHAVE_PTMX -DNO_DNS_SRV -DNOGFTIMER \
        -DNOB_50 -DNOB_75 -DNOB_134 -DNOB_150 -DNOB_200 \
        -DNOB_1800 -DNOB_3600 -DNOB_7200 -DNOB_76K -DNOB_230K \
        -DNOB_460K -DNOB_921K \
        -DNOCSETS -DNONET -DNOUNICODE -DNOHELP -DNODEBUG \
        -DNOFRILLS -DNOFTP -DNODIAL -DNOPUSH -DNOIKSD -DNOHTTP -DNOFLOAT \
        -DNOSERVER -DNOSEXP -DNORLOGIN -DNOOLDMODEMS -DNOSSH -DNOLISTEN \
        -DNORESEND -DNOAUTODL -DNOSTREAMING -DNOHINTS -DNOCKXYZ -DNOLEARN \
        -DNOMKDIR -DNOPERMS -DNOCKTIMERS -DNOCKREGEX -DNOREALPATH \
        -DCK_SMALL -DNOLOGDIAL -DNORENAME -DNOWHATAMI \
        -DNOARROWKEYS -DMAINTYPE=int"
}

do_install () {
    install -d ${D}${BINDIR} ${D}${MANDIR} ${D}${INFODIR}
    oe_runmake 'DESTDIR=${D}' install
    # Fix up dangling symlink
    rm ${D}${BINDIR}/kermit-sshsub
    (cd ${D}${BINDIR} && ln -s ${BINDIR}/kermit kermit-sshusb)
}
