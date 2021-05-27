SUMMARY = "Midnight Commander is an ncurses based file manager"
HOMEPAGE = "http://www.midnight-commander.org/"
DESCRIPTION = "GNU Midnight Commander is a visual file manager, licensed under GNU General Public License and therefore qualifies as Free Software. It's a feature rich full-screen text mode application that allows you to copy, move and delete files and whole directory trees, search for files and run commands in the subshell. Internal viewer and editor are included."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=270bbafe360e73f9840bd7981621f9c2"
SECTION = "console/utils"
DEPENDS = "ncurses glib-2.0 util-linux"
RDEPENDS_${PN} = "ncurses-terminfo-base"
RRECOMMENDS_${PN} = "ncurses-terminfo"

SRC_URI = "http://www.midnight-commander.org/downloads/${BPN}-${PV}.tar.bz2 \
           file://0001-mc-replace-perl-w-with-use-warnings.patch \
           file://0001-Add-option-to-control-configure-args.patch \
           file://0001-Ticket-3629-configure.ac-drop-bundled-gettext.patch \
           file://nomandate.patch \
           "
SRC_URI[md5sum] = "152927ac29cf0e61d7d019f261bb7d89"
SRC_URI[sha256sum] = "238c4552545dcf3065359bd50753abbb150c1b22ec5a36eaa02c82808293267d"

inherit autotools gettext pkgconfig

#
# Both Samba (smb) and sftp require package delivered from meta-openembedded
#
PACKAGECONFIG ??= ""
PACKAGECONFIG[smb] = "--enable-vfs-smb,--disable-vfs-smb,samba,"
PACKAGECONFIG[sftp] = "--enable-vfs-sftp,--disable-vfs-sftp,libssh2,"

EXTRA_OECONF = "--with-screen=ncurses --without-gpm-mouse --without-x --disable-configure-args"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl'"
CACHED_CONFIGUREVARS += "ac_cv_path_PYTHON='/usr/bin/env python'"
CACHED_CONFIGUREVARS += "ac_cv_path_GREP='/usr/bin/env grep'"
CACHED_CONFIGUREVARS += "mc_cv_have_zipinfo=yes"

do_install_append () {
	sed -i -e '1s,#!.*perl,#!${bindir}/env perl,' ${D}${libexecdir}/mc/extfs.d/*
        
        rm ${D}${libexecdir}/mc/extfs.d/s3+ ${D}${libexecdir}/mc/extfs.d/uc1541
}

PACKAGES =+ "${BPN}-helpers-perl ${BPN}-helpers ${BPN}-fish"

SUMMARY_${BPN}-helpers-perl = "Midnight Commander Perl-based helper scripts"
FILES_${BPN}-helpers-perl = "${libexecdir}/mc/extfs.d/a+ ${libexecdir}/mc/extfs.d/apt+ \
                             ${libexecdir}/mc/extfs.d/deb ${libexecdir}/mc/extfs.d/deba \
                             ${libexecdir}/mc/extfs.d/debd ${libexecdir}/mc/extfs.d/dpkg+ \
                             ${libexecdir}/mc/extfs.d/mailfs ${libexecdir}/mc/extfs.d/patchfs \ 
                             ${libexecdir}/mc/extfs.d/rpms+ ${libexecdir}/mc/extfs.d/ulib \ 
                             ${libexecdir}/mc/extfs.d/uzip"
RDEPENDS_${BPN}-helpers-perl = "perl"

SUMMARY_${BPN}-helpers = "Midnight Commander shell helper scripts"
FILES_${BPN}-helpers = "${libexecdir}/mc/extfs.d/* ${libexecdir}/mc/ext.d/*"

SUMMARY_${BPN}-fish = "Midnight Commander Fish scripts"
FILES_${BPN}-fish = "${libexecdir}/mc/fish"
