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
SRCREV = "c266469347d49287be38059d45e7aaa454db9cb2"

DEPENDS = "bzip2 zlib elfutils xz"
RDEPENDS:${PN}-tools = "perl ${PN}"

# mips/rv32 would not compile.
COMPATIBLE_HOST:mipsarcho32 = "null"
COMPATIBLE_HOST:riscv32 = "null"

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

MAKEDUMPFILE_TARGET ?= "${TARGET_ARCH}"
MAKEDUMPFILE_TARGET:powerpc = "ppc"

EXTRA_OEMAKE = "\
    LINKTYPE=static \
    TARGET=${MAKEDUMPFILE_TARGET} \
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

    mkdir -p ${D}/etc/
    install -m 644 ${S}/makedumpfile.conf ${D}/etc/makedumpfile.conf.sample
}
