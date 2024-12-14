SUMMARY = "Multiplexing terminal manager"
DESCRIPTION = "Screen is a full-screen window manager \
that multiplexes a physical terminal between several \
processes, typically interactive shells."
HOMEPAGE = "http://www.gnu.org/software/screen/"
BUGTRACKER = "https://savannah.gnu.org/bugs/?func=additem&group=screen"

SECTION = "console/utils"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e \
                    file://screen.h;endline=26;md5=b8dc717c9a3dba842ae6c44ca0f73f52 \
                    "

DEPENDS = "ncurses virtual/crypt \
          ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
RDEPENDS:${PN} = "base-files"

SRC_URI = "${GNU_MIRROR}/screen/screen-${PV}.tar.gz \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'file://screen.pam', '', d)} \
           "

SRC_URI[sha256sum] = "f04a39d00a0e5c7c86a55338808903082ad5df4d73df1a2fd3425976aed94971"

inherit autotools-brokensep texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

EXTRA_OECONF = "--with-pty-mode=0620 --with-pty-group=5 --with-system_screenrc=${sysconfdir}/screenrc \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)}"

do_install:append () {
	install -D -m 644 ${S}/etc/etcscreenrc ${D}/${sysconfdir}/screenrc
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -D -m 644 ${UNPACKDIR}/screen.pam ${D}/${sysconfdir}/pam.d/screen
	fi
}

pkg_postinst:${PN} () {
	grep -q "^${bindir}/screen$" $D${sysconfdir}/shells || echo ${bindir}/screen >> $D${sysconfdir}/shells
}

pkg_postrm:${PN} () {
	printf "$(grep -v "^${bindir}/screen$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
