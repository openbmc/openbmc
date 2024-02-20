SUMMARY = "Cross locale generation tool for glibc"
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"

LIC_FILES_CHKSUM = "file://LICENSES;md5=f77e878d320e99e94ae9a4aea7f491d1 \
      file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

require glibc-version.inc

# Tell autotools that we're working in the localedef directory
#
AUTOTOOLS_SCRIPT_PATH = "${S}/localedef"

inherit autotools
inherit native

FILESEXTRAPATHS =. "${FILE_DIRNAME}/${PN}:${FILE_DIRNAME}/glibc:"

SRC_URI = "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           git://github.com/kraj/localedef;branch=master;name=localedef;destsuffix=git/localedef;protocol=https \
           \
           file://0001-localedef-Add-hardlink-resolver-from-util-linux.patch \
           file://0002-localedef-fix-ups-hardlink-to-make-it-compile.patch \
           \
           file://0010-eglibc-Cross-building-and-testing-instructions.patch \
           file://0011-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0012-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0013-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0014-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
           file://0017-Replace-echo-with-printf-builtin-in-nscd-init-script.patch \
           file://0019-timezone-Make-shell-interpreter-overridable-in-tzsel.patch \
           "
# Makes for a rather long rev (22 characters), but...
#
SRCREV_FORMAT = "glibc_localedef"

S = "${WORKDIR}/git"

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
