SUMMARY = "Multiplexing terminal manager"
DESCRIPTION = "Screen is a full-screen window manager \
that multiplexes a physical terminal between several \
processes, typically interactive shells."
HOMEPAGE = "http://www.gnu.org/software/screen/"
BUGTRACKER = "https://savannah.gnu.org/bugs/?func=additem&group=screen"

SECTION = "console/utils"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://screen.h;endline=26;md5=3971142989289a8198a544220703c2bf"

DEPENDS = "ncurses \
          ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
RDEPENDS_${PN} = "base-files"

SRC_URI = "${GNU_MIRROR}/screen/screen-${PV}.tar.gz \
           file://fix-parallel-make.patch \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'file://screen.pam', '', d)} \
           file://Remove-redundant-compiler-sanity-checks.patch \
           file://Provide-cross-compile-alternatives-for-AC_TRY_RUN.patch \
           file://Skip-host-file-system-checks-when-cross-compiling.patch \
           file://Avoid-mis-identifying-systems-as-SVR4.patch \
           file://0002-comm.h-now-depends-on-term.h.patch \
           file://0001-fix-for-multijob-build.patch \
          "

SRC_URI[md5sum] = "d26e11a3648c2b11aaefa215a55dfd39"
SRC_URI[sha256sum] = "ef722a54759a3bf23aad272bbf33c414c1078cad6bcd982fada93c0d7917218b"

inherit autotools texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG[utempter] = "ac_cv_header_utempter_h=yes,ac_cv_header_utempter_h=no,libutempter,"

EXTRA_OECONF = "--with-pty-mode=0620 --with-pty-group=5 \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)}"

do_install_append () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -D -m 644 ${WORKDIR}/screen.pam ${D}/${sysconfdir}/pam.d/screen
	fi
}

pkg_postinst_${PN} () {
	grep -q "^${bindir}/screen$" $D${sysconfdir}/shells || echo ${bindir}/screen >> $D${sysconfdir}/shells
}

pkg_postrm_${PN} () {
	printf "$(grep -v "^${bindir}/screen$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
