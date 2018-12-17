SUMMARY = "Cross locale generation tool for glibc"
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"
SECTION = "libs"
LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://LICENSES;md5=cfc0ed77a9f62fa62eded042ebe31d72 \
      file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
      file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

# Tell autotools that we're working in the localedef directory
#
AUTOTOOLS_SCRIPT_PATH = "${S}/localedef"

inherit native
inherit autotools

FILESEXTRAPATHS =. "${FILE_DIRNAME}/${PN}:${FILE_DIRNAME}/glibc:"

SRCBRANCH ?= "release/${PV}/master"
GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"

SRCREV_glibc ?= "3c03baca37fdcb52c3881e653ca392bba7a99c2b"
SRCREV_localedef ?= "c328777219ccc480be3112cf807217ca6b570b64"

SRC_URI = "${GLIBC_GIT_URI};branch=${SRCBRANCH};name=glibc \
           git://github.com/kraj/localedef;branch=master;name=localedef;destsuffix=git/localedef \
           file://0016-timezone-re-written-tzselect-as-posix-sh.patch \
           file://0017-Remove-bash-dependency-for-nscd-init-script.patch \
           file://0018-eglibc-Cross-building-and-testing-instructions.patch \
           file://0019-eglibc-Help-bootstrap-cross-toolchain.patch \
           file://0020-eglibc-Clear-cache-lines-on-ppc8xx.patch \
           file://0021-eglibc-Resolve-__fpscr_values-on-SH4.patch \
           file://0022-eglibc-Forward-port-cross-locale-generation-support.patch \
           file://0023-Define-DUMMY_LOCALE_T-if-not-defined.patch \
           file://0029-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
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
}
