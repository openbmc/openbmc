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

# arm and aarch64 would compile but has never been tested upstream.  mips would not compile.
#
COMPATIBLE_HOST = "(x86_64|i.86|powerpc|arm|aarch64).*-linux"

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*.pl"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/makedumpfile/${BPN}-${PV}.tar.gz \
    file://0001-makedumpfile-replace-hardcode-CFLAGS.patch \
    file://0002-mem_section-Support-only-46-bit-for-MAX_PHYSMEM_BITS.patch \
"
SRC_URI[md5sum] = "7dfbca19f487604c271e2892ba07f4bd"
SRC_URI[sha256sum] = "7e06f72d5f291fcab9e92975f405a76e37d4f7fc8fa4172f199636398ae812b1"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/makedumpfile/files/makedumpfile/"
UPSTREAM_CHECK_REGEX = "makedumpfile/(?P<pver>\d+(\.\d+)+)/"

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
