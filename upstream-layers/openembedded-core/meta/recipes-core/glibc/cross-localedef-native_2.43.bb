SUMMARY = "Cross locale generation tool for glibc"
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"

LIC_FILES_CHKSUM = "file://LICENSES;md5=df6e0948b55669789c30764c5fd9bc41 \
      file://COPYINGv2;md5=570a9b3749dd0463a1778803b12a6dce \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LESSERv2;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

require glibc-version.inc

# Tell autotools that we're working in the localedef directory
#
AUTOTOOLS_SCRIPT_PATH = "${S}/localedef"

inherit autotools
inherit native

FILESEXTRAPATHS =. "${FILE_DIRNAME}/${PN}:${FILE_DIRNAME}/glibc:"

SRC_URI = "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           git://github.com/kraj/localedef;branch=master;name=localedef;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/localedef;protocol=https \
           \
           file://0001-localedef-Add-hardlink-resolver-from-util-linux.patch \
           file://0002-localedef-fix-ups-hardlink-to-make-it-compile.patch \
           \
           file://0009-eglibc-Cross-building-and-testing-instructions.patch \
           file://0010-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0011-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0012-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0013-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
           file://0016-Replace-echo-with-printf-builtin-in-nscd-init-script.patch \
           file://0018-timezone-Make-shell-interpreter-overridable-in-tzsel.patch \
           "
# Makes for a rather long rev (22 characters), but...
#
SRCREV_FORMAT = "glibc_localedef"

EXTRA_OECONF = "--with-glibc=${S}"

# We do not need bash to run tzselect script, the default is to use
# bash but it can be configured by setting KSHELL Makefile variable
EXTRA_OEMAKE += "KSHELL=/bin/sh"

CFLAGS += "-fgnu89-inline -std=gnu99 -DIS_IN\(x\)='0'"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/localedef ${D}${bindir}/cross-localedef
	install -m 0755 ${B}/cross-localedef-hardlink ${D}${bindir}/cross-localedef-hardlink
}
