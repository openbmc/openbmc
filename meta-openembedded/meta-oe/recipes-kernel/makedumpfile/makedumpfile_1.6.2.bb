SUMMARY = "VMcore extraction tool"
DESCRIPTION = "\
    This program is used to extract a subset of the memory available either \
    via /dev/mem or /proc/vmcore (for crashdumps). It is used to get memory \
    images without extra uneeded information (zero pages, userspace programs, \
    etc). \
"
HOMEPAGE = "http://makedumpfile.sourceforge.net"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
LICENSE = "GPLv2.0"

DEPENDS = "bzip2 zlib elfutils"
RDEPENDS_${PN}-tools = "perl ${PN}"

# arm would compile but has never been tested upstream.  mips would not compile.
#
COMPATIBLE_HOST = "(x86_64|i.86|powerpc|arm).*-linux"

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*.pl"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/makedumpfile/${BPN}-${PV}.tar.gz \
    file://0001-makedumpfile-replace-hardcode-CFLAGS.patch \
    file://0002-mem_section-Support-only-46-bit-for-MAX_PHYSMEM_BITS.patch \
"
SRC_URI[md5sum] = "72be5bf973eed898c5127e3abc466583"
SRC_URI[sha256sum] = "ee3d21cb4032a60bd2c802d1d8b21b9f399710e1574f8de8ead5ab50cd821a6d"

SECTION = "base"

# If we do not specify TARGET, makedumpfile will build for the host but use the
# target gcc.
#
EXTRA_OEMAKE = "\
    LINKTYPE=static \
    TARGET=${TARGET_ARCH} \
"

do_install () {
    mkdir -p ${D}/usr/bin
    install -m 755 ${S}/makedumpfile ${D}/usr/bin
    install -m 755 ${S}/makedumpfile-R.pl ${D}/usr/bin

    mkdir -p ${D}/usr/share/man/man8
    install -m 644 ${S}/makedumpfile.8.gz ${D}/usr/share/man/man8

    mkdir -p ${D}/usr/share/man/man5
    install -m 644 ${S}/makedumpfile.conf.5.gz ${D}/usr/share/man/man5

    mkdir -p ${D}/etc/
    install -m 644 ${S}/makedumpfile.conf ${D}/etc/makedumpfile.conf.sample
}
