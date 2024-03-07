SUMMARY = "Midnight Commander is an ncurses based file manager"
HOMEPAGE = "http://www.midnight-commander.org/"
DESCRIPTION = "GNU Midnight Commander is a visual file manager, licensed under GNU General Public License and therefore qualifies as Free Software. It's a feature rich full-screen text mode application that allows you to copy, move and delete files and whole directory trees, search for files and run commands in the subshell. Internal viewer and editor are included."
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=270bbafe360e73f9840bd7981621f9c2"
SECTION = "console/utils"
DEPENDS = "ncurses glib-2.0 util-linux file-replacement-native"
RDEPENDS:${PN} = "ncurses-terminfo-base"
RRECOMMENDS:${PN} = "ncurses-terminfo"

SRC_URI = "http://www.midnight-commander.org/downloads/${BPN}-${PV}.tar.bz2 \
           file://nomandate.patch \
           "
SRC_URI[sha256sum] = "f42f4114ed42f6cf9995f1d896fa6c797ccb36dac57760dda8dd9f78ac462841"

inherit autotools gettext pkgconfig

#
# Both Samba (smb) and sftp require package delivered from meta-openembedded
#
PACKAGECONFIG ??= ""
PACKAGECONFIG[sftp] = "--enable-vfs-sftp,--disable-vfs-sftp,libssh2,"

# enable NCURSES_WIDECHAR=1 only if ENABLE_WIDEC has not been explicitly disabled (e.g. by the distro config).
# When compiling against the ncurses library, NCURSES_WIDECHAR needs to explicitly set to 0 in this case.
CFLAGS:append:libc-musl = "${@' -DNCURSES_WIDECHAR=1' if bb.utils.to_boolean((d.getVar('ENABLE_WIDEC') or 'True')) else ' -DNCURSES_WIDECHAR=0'}"
EXTRA_OECONF = "--with-screen=ncurses --without-gpm-mouse --without-x --disable-configure-args"
EXTRANATIVEPATH += "file-native"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl'"
CACHED_CONFIGUREVARS += "ac_cv_path_PERL_FOR_BUILD='/usr/bin/env perl'"
CACHED_CONFIGUREVARS += "ac_cv_path_PYTHON='/usr/bin/env python'"
CACHED_CONFIGUREVARS += "ac_cv_path_GREP='/usr/bin/env grep'"
CACHED_CONFIGUREVARS += "mc_cv_have_zipinfo=yes"

do_install:append () {
	sed -i -e '1s,#!.*perl,#!${bindir}/env perl,' ${D}${libexecdir}/mc/extfs.d/*
        
        rm ${D}${libexecdir}/mc/extfs.d/s3+ ${D}${libexecdir}/mc/extfs.d/uc1541
}

PACKAGES =+ "${BPN}-helpers-perl ${BPN}-helpers ${BPN}-shell"

SUMMARY:${BPN}-helpers-perl = "Midnight Commander Perl-based helper scripts"
FILES:${BPN}-helpers-perl = "${libexecdir}/mc/extfs.d/a+ ${libexecdir}/mc/extfs.d/apt+ \
                             ${libexecdir}/mc/extfs.d/deb ${libexecdir}/mc/extfs.d/deba \
                             ${libexecdir}/mc/extfs.d/debd ${libexecdir}/mc/extfs.d/dpkg+ \
                             ${libexecdir}/mc/extfs.d/mailfs ${libexecdir}/mc/extfs.d/patchfs \ 
                             ${libexecdir}/mc/extfs.d/rpms+ ${libexecdir}/mc/extfs.d/ulib \ 
                             ${libexecdir}/mc/extfs.d/uzip"
RDEPENDS:${BPN}-helpers-perl = "perl"

SUMMARY:${BPN}-helpers = "Midnight Commander shell helper scripts"
FILES:${BPN}-helpers = "${libexecdir}/mc/extfs.d/* ${libexecdir}/mc/ext.d/*"

SUMMARY:${BPN}-shell = "Midnight Commander Shell scripts"
FILES:${BPN}-shell = "${libexecdir}/mc/shell"
