SUMMARY = "Convert text file line endings between CRLF and LF"
DESCRIPTION = "The Dos2unix package includes utilities dos2unix and \
unix2dos to convert plain text files in DOS or Mac format to Unix \
format and vice versa."
HOMEPAGE = "http://waterlan.home.xs4all.nl/dos2unix.html"
SECTION = "support"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=8a7c3499a1142df819e727253cd53a12"

SRC_URI = "git://git.code.sf.net/p/dos2unix/dos2unix;branch=master"
UPSTREAM_CHECK_GITTAGREGEX = "dos2unix-(?P<pver>(\d+(\.\d+)+))"

SRCREV = "72596f0ae21faa25a07a872d4843bc885475115d"

S = "${WORKDIR}/git/dos2unix"

inherit gettext perlnative

# The dos2unix NLS relies on po4a-native, while po4a recipe is
# provided by meta-perl layer, so make it optional here, you
# need have meta-perl in bblayers.conf before enabling nls in
# PACKAGECONFIG.
PACKAGECONFIG ??= ""
PACKAGECONFIG[nls] = "ENABLE_NLS=1,ENABLE_NLS=,po4a-native"

EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS} LDFLAGS_USER='${LDFLAGS}'"
EXTRA_OEMAKE:class-native = "ENABLE_NLS="

do_install () {
	oe_runmake DESTDIR="${D}${base_prefix}" install
}

BBCLASSEXTEND = "native nativesdk"
