SUMMARY = "Generic data test program"
DESCRIPTION = "The Data Test Program (dt) is a generic data test program used to verify proper \
operation of peripherals, file systems, device drivers, or any data stream supported by the \
operating system."
HOMEPAGE = "http://www.scsifaq.org/RMiller_Tools/dt.html"

SECTION = "console/tests"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=be8bb25bbcfaa0725710d188e5152668"

# Source URI taken from Fedora RPM spec file at:
#  http://pkgs.fedoraproject.org/git/rpms/dt.git
SRC_URI = "http://dl.dropboxusercontent.com/u/32363629/Datatest/dt-source-v${PV}.tar.gz \
           file://dt-default-source-define.patch \
           file://dt-wformat-security.patch \
           file://Stop-using-relative-path-for-scsilib.c-link.patch \
           file://Use-tcsh-shell.patch \
"

SRC_URI[md5sum] = "3054aeaaba047a1dbe90c2132a382ee2"
SRC_URI[sha256sum] = "10d164676e918a4d07f233bcd11e4cb6bfd1052c996182cd1827ccd0c063fcc6"

S = "${WORKDIR}/dt.v${PV}"

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE += "-f Makefile.linux \
                 OS=linux \
                 CFLAGS="-I.. -DAIO -DFIFO -DMMAP -D__linux__ -D_GNU_SOURCE -D_FILE_OFFSET_BITS=64 -DTHREADS -DSCSI""

do_compile() {
    oe_runmake
}

do_install() {
    install -Dm755 dt ${D}${sbindir}/dt
    install -Dm644 Documentation/dt.man ${D}${mandir}/man8/dt.8

    install -d ${D}${datadir}/dt/
    install -d ${D}${docdir}/dt/html/
    install -m755 Scripts/dt? ${D}${datadir}/dt/
    install -m644 data/pattern_* ${D}${datadir}/dt/
    install -m644 html/* ${D}${docdir}/dt/html/
}

RDEPENDS_${PN} += "tcsh"
