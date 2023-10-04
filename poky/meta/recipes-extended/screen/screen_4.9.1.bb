SUMMARY = "Multiplexing terminal manager"
DESCRIPTION = "Screen is a full-screen window manager \
that multiplexes a physical terminal between several \
processes, typically interactive shells."
HOMEPAGE = "http://www.gnu.org/software/screen/"
BUGTRACKER = "https://savannah.gnu.org/bugs/?func=additem&group=screen"

SECTION = "console/utils"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://screen.h;endline=26;md5=b8dc717c9a3dba842ae6c44ca0f73f52 \
                    "

DEPENDS = "ncurses virtual/crypt \
          ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
RDEPENDS:${PN} = "base-files"

SRC_URI = "${GNU_MIRROR}/screen/screen-${PV}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'file://screen.pam', '', d)} \
           file://0002-comm.h-now-depends-on-term.h.patch \
           file://0001-fix-for-multijob-build.patch \
           file://0001-Remove-more-compatibility-stuff.patch \
           "

SRC_URI[sha256sum] = "26cef3e3c42571c0d484ad6faf110c5c15091fbf872b06fa7aa4766c7405ac69"

inherit autotools texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

EXTRA_OECONF = "--with-pty-mode=0620 --with-pty-group=5 --with-sys-screenrc=${sysconfdir}/screenrc \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)}"

do_install:append () {
	install -D -m 644 ${S}/etc/etcscreenrc ${D}/${sysconfdir}/screenrc
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -D -m 644 ${WORKDIR}/screen.pam ${D}/${sysconfdir}/pam.d/screen
	fi
}

pkg_postinst:${PN} () {
	grep -q "^${bindir}/screen$" $D${sysconfdir}/shells || echo ${bindir}/screen >> $D${sysconfdir}/shells
}

pkg_postrm:${PN} () {
	printf "$(grep -v "^${bindir}/screen$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
