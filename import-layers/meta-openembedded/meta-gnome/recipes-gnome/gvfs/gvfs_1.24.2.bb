DESCRIPTION = "gvfs is a userspace virtual filesystem"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=05df38dd77c35ec8431f212410a3329e"

inherit gnome

DEPENDS += "libsecret glib-2.0 gconf intltool-native libgudev udisks2"

SRC_URI[archive.md5sum] = "83ed317eb2a5264715d4273e90a5cfd8"
SRC_URI[archive.sha256sum] = "0949eaedd1df7175f8d7ee2700df8210d1f854b8ee37d963bc32ee7091eeb228"
SRC_URI += " \
    file://0001-Add-support-for-libsystemd.patch \
"

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
"
RDEPENDS_${PN} = "udisks2"

FILES_${PN}-dbg += "${libdir}/gio/modules/.debug/*"
FILES_${PN}-dev += "${libdir}/gio/modules/*.la"

FILES_gvfsd-ftp = "${libexecdir}/gvfsd-ftp ${datadir}/gvfs/mounts/ftp.mount"
FILES_gvfsd-sftp = "${libexecdir}/gvfsd-sftp ${datadir}/gvfs/mounts/sftp.mount"
FILES_gvfsd-trash = "${libexecdir}/gvfsd-trash ${datadir}/gvfs/mounts/trash.mount"

RRECOMMENDS_gvfsd-ftp += "openssh-sftp openssh-ssh"

PACKAGES += "${PN}-bash-completion"
FILES_${PN}-bash-completion = "${datadir}/bash-completion"
RDEPENDS_${PN}-bash-completion = "bash-completion"

PACKAGECONFIG ?= "libgphoto2"

PACKAGECONFIG[afc] = "--enable-afc, --disable-afc, libimobiledevice libplist"
PACKAGECONFIG[archive] = "--enable-archive, --disable-archive, libarchive"
PACKAGECONFIG[avahi] = "--enable-avahi, --disable-avahi, avahi"
PACKAGECONFIG[gtk] = "--enable-gtk, --disable-gtk, gtk+3"
PACKAGECONFIG[http] = "--enable-http, --disable-http, libsoup-2.4"
PACKAGECONFIG[libmtp] = "--enable-libmtp, --disable-libmtp, libmtp"
PACKAGECONFIG[libgphoto2] = "--enable-gphoto2, --disable-gphoto2, libgphoto2"
PACKAGECONFIG[samba] = "--enable-samba, --disable-samba, samba"

# needs meta-filesystems
PACKAGECONFIG[fuse] = "--enable-fuse, --disable-fuse, fuse"

# libcdio-paranoia recipe doesn't exist yet
PACKAGECONFIG[cdda] = "--enable-cdda, --disable-cdda, libcdio-paranoia"
