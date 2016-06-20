SUMMARY = "Library for editing typed command lines"
DESCRIPTION = "The GNU Readline library provides a set of functions for use by applications that allow users to edit \
command lines as they are typed in. Both Emacs and vi editing modes are available. The Readline library includes  \
additional functions to maintain a list of previously-entered command lines, to recall and perhaps reedit those   \
lines, and perform csh-like history expansion on previous commands."
SECTION = "libs"

# GPLv2+ (< 6.0), GPLv3+ (>= 6.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "ncurses"

SRC_URI = "${GNU_MIRROR}/readline/${BPN}-${PV}.tar.gz;name=archive"
SRC_URI += "file://inputrc"

inherit autotools texinfo

EXTRA_AUTORECONF += "--exclude=autoheader"

LEAD_SONAME = "libreadline.so"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cat ${S}/aclocal.m4 > ${S}/acinclude.m4
	fi
}

do_install_append () {
	# Make install doesn't properly install these
	oe_libinstall -so -C shlib libhistory ${D}${libdir}
	oe_libinstall -so -C shlib libreadline ${D}${libdir}

	rmdir ${D}${bindir}

	install -m 0755 -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/inputrc ${D}${sysconfdir}/inputrc
}

BBCLASSEXTEND = "native nativesdk"

CONFFILES_${PN} += "${sysconfdir}/inputrc"
