SUMMARY = "a userspace virtual filesystem"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://libgnomevfs/gnome-vfs.h;endline=25;md5=17071e69707a8f42887d88413f2623cb \
                    file://programs/gnomevfs-ls.c;endline=23;md5=678a2e8bedaef5818ccabe633840196b \
                    file://daemon/vfs-daemon.c;endline=21;md5=5f2c61553fb16abb07fc9498ca03fe1f \
                    file://modules/cdda-cddb.h;endline=22;md5=20ed324ca64907c366ecd7f22b8e0c54"

DEPENDS = "libxml2 gconf dbus bzip2 gnome-mime-data zlib intltool-native gnome-common-native"
RRECOMMENDS_${PN} = "gnome-vfs-plugin-file shared-mime-info"
# Some legacy packages will require gnome-mime-data to be installed, but use of
# it is deprecated.
PR = "r3"

inherit gnome

# This is to provide compatibility with the gnome-vfs DBus fork
RPROVIDES_${PN} = "gnome-vfs-plugin-dbus"

SRC_URI += " \
    file://gconftool-lossage.patch \
    file://gnome-vfs-no-kerberos.patch;striplevel=0 \
    file://0001-multiple-Makefile.am-remove-DG_DISABLE_DEPRECATED-to.patch \
    file://do-not-use-srcdir-variable.patch \
"

SRC_URI[archive.md5sum] = "a05fab03eeef10a47dd156b758982f2e"
SRC_URI[archive.sha256sum] = "62de64b5b804eb04104ff98fcd6a8b7276d510a49fbd9c0feb568f8996444faa"
GNOME_COMPRESS_TYPE="bz2"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)}"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[fam] = "--enable-fam,--disable-fam,gamin"

EXTRA_OECONF = " --disable-hal \
                 --disable-openssl \
                 --disable-samba \
                 ac_cv_lib_acl_acl_get_file=no \
                 ac_cv_lib_sec_acl=no \
                 gvfs_cv_HAVE_SOLARIS_ACLS=no \
                 gvfs_cv_HAVE_POSIX_ACLS=no \
"

FILES_${PN} += "${libdir}/vfs ${datadir}/dbus-1/services"
FILES_${PN}-dbg += "${libdir}/gnome-vfs-2.0/modules/.debug"
FILES_${PN}-dev += "${libdir}/gnome-vfs-2.0/include/* ${libdir}/gnome-vfs-2.0/modules/*.la"
FILES_${PN}-staticdev += "${libdir}/gnome-vfs-2.0/modules/*.a"
FILES_${PN}-doc += "${datadir}/gtk-doc"

PACKAGES_DYNAMIC += "^gnome-vfs-plugin-.*"

python populate_packages_prepend () {
    plugindir = d.expand('${libdir}/gnome-vfs-2.0/modules/')
    do_split_packages(d, plugindir, '^lib(.*)\.so$', 'gnome-vfs-plugin-%s', 'GNOME VFS plugin for %s')
}
