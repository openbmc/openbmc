DESCRIPTION = "gvfs is a userspace virtual filesystem"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=05df38dd77c35ec8431f212410a3329e"

inherit gnome bash-completion gettext

DEPENDS += "libsecret glib-2.0 gconf intltool-native libgudev udisks2 polkit shadow-native"

SRC_URI = "https://download.gnome.org/sources/${BPN}/${@gnome_verdir("${PV}")}/${BPN}-${PV}.tar.xz;name=archive"

SRC_URI[archive.md5sum] = "cbe766b46f324e17d7abcfb4a89a1684"
SRC_URI[archive.sha256sum] = "d0b6c9edab09d52472355657a2f0a14831b2e6c58caba395f721ab683f836ade"

EXTRA_OECONF = " \
    --disable-gdu \
    --enable-udisks2 \
    --disable-documentation \
    --with-archive-includes=${STAGING_INCDIR} \
    --with-archive-libs=${STAGING_LIBDIR} \
"

PACKAGES =+ "gvfsd-ftp gvfsd-sftp gvfsd-trash"

FILES_${PN} += " \
    ${datadir}/glib-2.0 \
    ${datadir}/GConf \
    ${datadir}/dbus-1/services \
    ${libdir}/gio/modules/*.so \
    ${libdir}/tmpfiles.d \
    ${systemd_user_unitdir} \
"
RDEPENDS_${PN} = "udisks2"

FILES_${PN}-dbg += "${libdir}/gio/modules/.debug/*"
FILES_${PN}-dev += "${libdir}/gio/modules/*.la"

FILES_gvfsd-ftp = "${libexecdir}/gvfsd-ftp ${datadir}/gvfs/mounts/ftp.mount"
FILES_gvfsd-sftp = "${libexecdir}/gvfsd-sftp ${datadir}/gvfs/mounts/sftp.mount"
FILES_gvfsd-trash = "${libexecdir}/gvfsd-trash ${datadir}/gvfs/mounts/trash.mount"

RRECOMMENDS_gvfsd-ftp += "openssh-sftp openssh-ssh"

PACKAGECONFIG ?= "libgphoto2 ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[afc] = "--enable-afc, --disable-afc, libimobiledevice libplist"
PACKAGECONFIG[archive] = "--enable-archive, --disable-archive, libarchive"
PACKAGECONFIG[avahi] = "--enable-avahi, --disable-avahi, avahi"
PACKAGECONFIG[gcr] = "--enable-gcr, --disable-gcr, gcr"
PACKAGECONFIG[gtk] = "--enable-gtk, --disable-gtk, gtk+3"
PACKAGECONFIG[http] = "--enable-http, --disable-http, libsoup-2.4"
PACKAGECONFIG[libmtp] = "--enable-libmtp, --disable-libmtp, libmtp"
PACKAGECONFIG[libgphoto2] = "--enable-gphoto2, --disable-gphoto2, libgphoto2"
PACKAGECONFIG[samba] = "--enable-samba, --disable-samba, samba"
PACKAGECONFIG[systemd] = "--with-systemduserunitdir=${systemd_user_unitdir},--without-systemduserunitdir,systemd"

# needs meta-filesystems
PACKAGECONFIG[fuse] = "--enable-fuse, --disable-fuse, fuse"

# libcdio-paranoia recipe doesn't exist yet
PACKAGECONFIG[cdda] = "--enable-cdda, --disable-cdda, libcdio-paranoia"

# Fix up permissions on polkit rules.d to work with rpm4 constraints
do_install_append() {
	chmod 700 ${D}/${datadir}/polkit-1/rules.d
	chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
}
