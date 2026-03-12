SUMMARY = "Convert text file line endings between CRLF and LF"
DESCRIPTION = "The Dos2unix package includes utilities dos2unix and \
unix2dos to convert plain text files in DOS or Mac format to Unix \
format and vice versa."
HOMEPAGE = "http://waterlan.home.xs4all.nl/dos2unix.html"
SECTION = "support"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=af1174fd3d1a254eec3a4a1d1da170a9"

SRC_URI = "git://git.code.sf.net/p/dos2unix/dos2unix;branch=master;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "dos2unix-(?P<pver>(\d+(\.\d+)+))"

SRCREV = "dd37537e5fe03ac123aca9a9c944ef87fb998dcd"

S = "${UNPACKDIR}/${BP}/dos2unix"

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
