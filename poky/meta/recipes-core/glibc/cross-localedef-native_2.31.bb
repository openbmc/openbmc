SUMMARY = "Cross locale generation tool for glibc"
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"
SECTION = "libs"
LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://LICENSES;md5=1541fd8f5e8f1579512bf05f533371ba \
      file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

require glibc-version.inc

# Tell autotools that we're working in the localedef directory
#
AUTOTOOLS_SCRIPT_PATH = "${S}/localedef"

inherit native
inherit autotools

FILESEXTRAPATHS =. "${FILE_DIRNAME}/${PN}:${FILE_DIRNAME}/glibc:"

SRC_URI = "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           git://github.com/kraj/localedef;branch=master;name=localedef;destsuffix=git/localedef \
           \
           file://0001-localedef-Add-hardlink-resolver-to-build.patch;patchdir=localedef \
           \
           file://0001-localedef-Add-hardlink-resolver-from-util-linux.patch \
           file://0002-localedef-fix-ups-hardlink-to-make-it-compile.patch \
           \
           file://0018-timezone-re-written-tzselect-as-posix-sh.patch \
           file://0019-Remove-bash-dependency-for-nscd-init-script.patch \
           file://0020-eglibc-Cross-building-and-testing-instructions.patch \
           file://0021-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0022-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0023-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0024-Define-DUMMY_LOCALE_T-if-not-defined.patch \
           file://0025-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
"
# Makes for a rather long rev (22 characters), but...
#
SRCREV_FORMAT = "glibc_localedef"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--with-glibc=${S}"
CFLAGS += "-fgnu89-inline -std=gnu99 -DIS_IN\(x\)='0'"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/localedef ${D}${bindir}/cross-localedef
	install -m 0755 ${B}/cross-localedef-hardlink ${D}${bindir}/cross-localedef-hardlink
}
