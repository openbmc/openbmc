DESCRIPTION = "gvfs is a userspace virtual filesystem"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=05df38dd77c35ec8431f212410a3329e"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase bash-completion gettext upstream-version-is-even

DEPENDS += "libsecret glib-2.0 glib-2.0-native gconf libgudev shadow-native"

SRC_URI = "https://download.gnome.org/sources/${BPN}/${@gnome_verdir("${PV}")}/${BPN}-${PV}.tar.xz;name=archive"

SRC_URI[archive.md5sum] = "9a9a97a63bb9e87258d48086a6bdacaa"
SRC_URI[archive.sha256sum] = "f311b7c5f855ebe259960c00c7d03c812e040928ecb0ec093b50a4f16b3bcf26"


EXTRA_OEMESON = " \
    -Dbluray=false \
    -Dgoa=false \
    -Dgoogle=false \
    -Dnfs=false \
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

FILES_${PN}-dbg += "${libdir}/gio/modules/.debug/*"
FILES_${PN}-dev += "${libdir}/gio/modules/*.la"

FILES_gvfsd-ftp = "${libexecdir}/gvfsd-ftp ${datadir}/gvfs/mounts/ftp.mount"
FILES_gvfsd-sftp = "${libexecdir}/gvfsd-sftp ${datadir}/gvfs/mounts/sftp.mount"
FILES_gvfsd-trash = "${libexecdir}/gvfsd-trash ${datadir}/gvfs/mounts/trash.mount"

RRECOMMENDS_gvfsd-ftp += "openssh-sftp openssh-ssh"

PACKAGECONFIG ?= "libgphoto2 \
                  ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES','polkit','udisks2','',d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES','polkit','admin','',d)} \
                 "

PACKAGECONFIG[udisks2] = "-Dudisks2=true, -Dudisks2=false, udisks2, udisks2"
PACKAGECONFIG[admin] = "-Dadmin=true, -Dadmin=false, libcap polkit"
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

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'true', 'false', d)}; then
        # Fix up permissions on polkit rules.d to work with rpm4 constraints
        chmod 700 ${D}/${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
    fi

    # After rebuilds (not from scracth) it can happen that the executables in
    # libexec ar missing executable permission flag. Not sure but it came up
    # during transition to meson. Looked into build files and logs but could
    # not find suspicious
    for exe in `find ${D}/${libexecdir}`; do
       chmod +x $exe
    done
}
