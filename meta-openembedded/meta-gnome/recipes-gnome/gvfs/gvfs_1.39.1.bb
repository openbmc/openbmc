DESCRIPTION = "gvfs is a userspace virtual filesystem"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=05df38dd77c35ec8431f212410a3329e"

GNOMEBASEBUILDCLASS = "meson"
inherit gnome bash-completion gettext

DEPENDS += "libsecret glib-2.0 gconf intltool-native libgudev udisks2 polkit shadow-native"

SRC_URI = "https://download.gnome.org/sources/${BPN}/${@gnome_verdir("${PV}")}/${BPN}-${PV}.tar.xz;name=archive"

SRC_URI[archive.md5sum] = "4c68899cfd80d57a153771534193bb9a"
SRC_URI[archive.sha256sum] = "6e7213570389b17b67433695f515f3b17705ba976168a4c8828fc177cce50223"


EXTRA_OEMESON = " \
    -Dbluray=false \
    -Dgdu=false \
    -Dgoa=false \
    -Dgoogle=false \
    -Dnfs=false \
    -Dudisks2=true \
    -Ddocumentation=false \
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

PACKAGECONFIG[afc] = "-Dafc=true, -Dafc=false, libimobiledevice libplist"
PACKAGECONFIG[archive] = "-Darchive=true, -Darchive=false, libarchive"
PACKAGECONFIG[dnssd] = "-Ddnssd=true, -Ddnssd=false, avahi"
PACKAGECONFIG[gcr] = "-Dgcr=true, -Dgcr=false, gcr"
PACKAGECONFIG[http] = "-Dhttp=true, -Dhttp=false, libsoup-2.4"
PACKAGECONFIG[libmtp] = "-Dmtp=true, -Dmtp=false, libmtp"
PACKAGECONFIG[logind] = "-Dlogind=true, -Dlogind=false, systemd"
PACKAGECONFIG[libgphoto2] = "-Dgphoto2=true, -Dgphoto2=false, libgphoto2"
PACKAGECONFIG[samba] = "-Dsmb=true, -Dsmb=false, samba"
PACKAGECONFIG[systemd] = "-Dsystemduserunitdir=${systemd_user_unitdir} -Dtmpfilesdir=${libdir}/tmpfiles.d, -Dsystemduserunitdir=no -Dtmpfilesdir=no, systemd"

# needs meta-filesystems
PACKAGECONFIG[fuse] = "-Dfuse=true, -Dfuse=false, fuse"

# libcdio-paranoia recipe doesn't exist yet
PACKAGECONFIG[cdda] = "-Dcdda=true, -Dcdda=false, libcdio-paranoia"

# Fix up permissions on polkit rules.d to work with rpm4 constraints
do_install_append() {
	chmod 700 ${D}/${datadir}/polkit-1/rules.d
	chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
}
