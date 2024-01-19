DESCRIPTION = "gvfs is a userspace virtual filesystem"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=05df38dd77c35ec8431f212410a3329e"

inherit gnomebase gsettings bash-completion gettext upstream-version-is-even features_check useradd

DEPENDS += "\
    dbus \
    glib-2.0 \
    glib-2.0-native \
    gsettings-desktop-schemas \
    libgudev \
    libsecret \
    libxml2 \
    shadow-native \
"

RDEPENDS:${PN} += "gsettings-desktop-schemas"

SRC_URI = "https://download.gnome.org/sources/${BPN}/${@gnome_verdir("${PV}")}/${BPN}-${PV}.tar.xz;name=archive"

SRC_URI[archive.sha256sum] = "cdbd4440f6d08792a6e7521244c17386e20bd537d375117099fc8fb68fe91741"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

EXTRA_OEMESON = " \
    -Dbluray=false \
"

PACKAGES =+ "gvfsd-ftp gvfsd-sftp gvfsd-trash"

FILES:${PN} += " \
    ${datadir}/glib-2.0 \
    ${datadir}/GConf \
    ${datadir}/dbus-1/services \
    ${libdir}/gio/modules/*.so \
    ${libdir}/tmpfiles.d \
    ${systemd_user_unitdir} \
"

FILES:${PN}-dbg += "${libdir}/gio/modules/.debug/*"
FILES:${PN}-dev += "${libdir}/gio/modules/*.la"

FILES:gvfsd-ftp = "${libexecdir}/gvfsd-ftp ${datadir}/gvfs/mounts/ftp.mount"
FILES:gvfsd-sftp = "${libexecdir}/gvfsd-sftp ${datadir}/gvfs/mounts/sftp.mount"
FILES:gvfsd-trash = "${libexecdir}/gvfsd-trash ${datadir}/gvfs/mounts/trash.mount"

RRECOMMENDS:gvfsd-ftp += "openssh-sftp openssh-ssh"

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
PACKAGECONFIG[gcr] = "-Dgcr=true, -Dgcr=false, gcr3"
PACKAGECONFIG[gcrypt] = "-Dgcrypt=true, -Dgcrypt=false, libgcrypt"
PACKAGECONFIG[goa] = "-Dgoa=true, -Dgoa=false, gnome-online-accounts"
PACKAGECONFIG[google] = "-Dgoogle=true, -Dgoogle=false, libgdata"
PACKAGECONFIG[http] = "-Dhttp=true, -Dhttp=false, libsoup-3.0"
PACKAGECONFIG[libmtp] = "-Dmtp=true, -Dmtp=false, libmtp"
PACKAGECONFIG[logind] = "-Dlogind=true, -Dlogind=false, systemd"
PACKAGECONFIG[libgphoto2] = "-Dgphoto2=true, -Dgphoto2=false, libgphoto2"
PACKAGECONFIG[nfs] = "-Dnfs=true, -Dnfs=false,libnfs"
PACKAGECONFIG[samba] = "-Dsmb=true, -Dsmb=false, samba"
PACKAGECONFIG[systemd] = "-Dsystemduserunitdir=${systemd_user_unitdir} -Dtmpfilesdir=${libdir}/tmpfiles.d, -Dsystemduserunitdir=no -Dtmpfilesdir=no, systemd"

# needs meta-filesystems
PACKAGECONFIG[fuse] = "-Dfuse=true, -Dfuse=false, fuse3"

# libcdio-paranoia recipe doesn't exist yet
PACKAGECONFIG[cdda] = "-Dcdda=true, -Dcdda=false, libcdio-paranoia"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'true', 'false', d)}; then
        # Fix up permissions on polkit rules.d to work with rpm4 constraints
        chmod 700 ${D}${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}${datadir}/polkit-1/rules.d
    fi

    # After rebuilds (not from scracth) it can happen that the executables in
    # libexec ar missing executable permission flag. Not sure but it came up
    # during transition to meson. Looked into build files and logs but could
    # not find suspicious
    for exe in `find ${D}/${libexecdir}`; do
       chmod +x $exe
    done
}
