SUMMARY = "VMcore extraction tool"
DESCRIPTION = "\
    This program is used to extract a subset of the memory available either \
    via /dev/mem or /proc/vmcore (for crashdumps). It is used to get memory \
    images without extra uneeded information (zero pages, userspace programs, \
    etc). \
"
HOMEPAGE = "https://github.com/makedumpfile/makedumpfile"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
LICENSE = "GPL-2.0-only"

SRCBRANCH ?= "master"
SRCREV = "06ef8e2b814feb08a668b7a3783c86674e49a7b1"

DEPENDS = "bzip2 zlib elfutils xz"
RDEPENDS:${PN}-tools = "perl ${PN}"

# mips would not compile.
COMPATIBLE_HOST = "(x86_64|i.86|powerpc|arm|aarch64).*-linux"

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*.pl"

SRC_URI = "\
    git://github.com/makedumpfile/makedumpfile;branch=${SRCBRANCH};protocol=https \
    file://0001-makedumpfile-replace-hardcode-CFLAGS.patch \
"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

SECTION = "base"

# If we do not specify TARGET, makedumpfile will build for the host but use the
# target gcc.
#
EXTRA_OEMAKE = "\
    LINKTYPE=static \
    TARGET=${TARGET_ARCH} \
    ${PACKAGECONFIG_CONFARGS} \
"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzo] = "USELZO=on,USELZO=off,lzo"
PACKAGECONFIG[snappy] = "USESNAPPY=on,USESNAPPY=off,snappy"
PACKAGECONFIG[zstd] = "USEZSTD=on,USEZSTD=off,zstd"

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
