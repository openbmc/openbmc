SUMMARY = "Midnight Commander is an ncurses based file manager"
HOMEPAGE = "http://www.midnight-commander.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=270bbafe360e73f9840bd7981621f9c2"
SECTION = "console/utils"
DEPENDS = "ncurses glib-2.0 util-linux"
RDEPENDS_${PN} = "ncurses-terminfo"

SRC_URI = "http://www.midnight-commander.org/downloads/${BPN}-${PV}.tar.bz2 \
           "
SRC_URI[md5sum] = "db48ce26ba425a61edc51930e94227fd"
SRC_URI[sha256sum] = "f74e3da70410975cdeb27ee9a66d1dd812250b6d3ede364bca728dd5a48e5471"

inherit autotools gettext pkgconfig

#
# Both Samba (smb) and sftp require package delivered from meta-openembedded
#
PACKAGECONFIG ??= ""
PACKAGECONFIG[smb] = "--enable-vfs-smb,--disable-vfs-smb,samba,"
PACKAGECONFIG[sftp] = "--enable-vfs-sftp,--disable-vfs-sftp,libssh2,"

EXTRA_OECONF = "--with-screen=ncurses --without-gpm-mouse --without-x"

do_install_append () {
	sed -i -e '1s,#!.*perl,#!${bindir}/env perl,' ${D}${libexecdir}/mc/extfs.d/*
	sed -i -e '1s,#!.*python,#!${bindir}/env python,' ${D}${libexecdir}/mc/extfs.d/*
}

PACKAGES =+ "${BPN}-helpers-perl ${BPN}-helpers-python ${BPN}-helpers ${BPN}-fish"

SUMMARY_${BPN}-helpers-perl = "Midnight Commander Perl-based helper scripts"
FILES_${BPN}-helpers-perl = "${libexecdir}/mc/extfs.d/a+ ${libexecdir}/mc/extfs.d/apt+ \
                             ${libexecdir}/mc/extfs.d/deb ${libexecdir}/mc/extfs.d/deba \
                             ${libexecdir}/mc/extfs.d/debd ${libexecdir}/mc/extfs.d/dpkg+ \
                             ${libexecdir}/mc/extfs.d/mailfs ${libexecdir}/mc/extfs.d/patchfs \ 
                             ${libexecdir}/mc/extfs.d/rpms+ ${libexecdir}/mc/extfs.d/ulib \ 
                             ${libexecdir}/mc/extfs.d/uzip"
RDEPENDS_${BPN}-helpers-perl = "perl"

SUMMARY_${BPN}-helpers-python = "Midnight Commander Python-based helper scripts"
FILES_${BPN}-helpers-python = "${libexecdir}/mc/extfs.d/s3+ ${libexecdir}/mc/extfs.d/uc1541"
RDEPENDS_${BPN}-helpers-python = "python"

SUMMARY_${BPN}-helpers = "Midnight Commander shell helper scripts"
FILES_${BPN}-helpers = "${libexecdir}/mc/extfs.d/* ${libexecdir}/mc/ext.d/*"

SUMMARY_${BPN}-fish = "Midnight Commander Fish scripts"
FILES_${BPN}-fish = "${libexecdir}/mc/fish"
