SUMMARY = "VMcore extraction tool"
DESCRIPTION = "\
    This program is used to extract a subset of the memory available either \
    via /dev/mem or /proc/vmcore (for crashdumps). It is used to get memory \
    images without extra uneeded information (zero pages, userspace programs, \
    etc). \
"
HOMEPAGE = "http://makedumpfile.sourceforge.net"

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
"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
LICENSE = "GPLv2.0"

SRC_URI[md5sum] = "041c5c6de2c3066600e4ca646c2397d6"
SRC_URI[sha256sum] = "e3147abc52df2ceac1e9affef45bf37e2f2e1d9979bc94a761ee11e4044072ac"

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
