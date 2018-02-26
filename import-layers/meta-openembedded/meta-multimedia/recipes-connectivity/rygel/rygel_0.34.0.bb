SUMMARY = "A UPnP AV media server and renderer"
DESCRIPTION = "Rygel is a home media solution (UPnP AV MediaServer) that \
allow you to easily share audio, video and pictures to other devices. \
Additionally, media player software may use Rygel to become a MediaRenderer \
that may be controlled remotely by a UPnP or DLNA Controller."
HOMEPAGE = "http://live.gnome.org/Rygel"

LICENSE = "LGPL1.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libxml2 glib-2.0 gssdp gupnp gupnp-av gupnp-dlna gstreamer1.0 gstreamer1.0-plugins-base ossp-uuid libgee libsoup-2.4 libmediaart-2.0 libunistring sqlite3 intltool-native"
RDEPENDS_${PN} = "gstreamer1.0-plugins-base-playback shared-mime-info"
RRECOMMENDS_${PN} = "rygel-plugin-media-export"

GNOME_COMPRESS_TYPE = "xz"
SRC_URI[archive.md5sum] = "7d8923324b280cd5452803d61176a34a"
SRC_URI[archive.sha256sum] = "fbb2d46cbe4e750df6610b4c66b2278f06d1f71a257c7903e676ccb394dad418"

inherit gnomebase vala gobject-introspection gettext systemd

EXTRA_OECONF = "--disable-tracker-plugin --with-media-engine=gstreamer"

PACKAGECONFIG ?= "external mpris ruih media-export gst-launch"

PACKAGECONFIG_append = "${@bb.utils.contains("DISTRO_FEATURES", "x11", " gtk+3", "", d)}"

PACKAGECONFIG[external] = "--enable-external-plugin,--disable-external-plugin"
PACKAGECONFIG[mpris] = "--enable-mpris-plugin,--disable-mpris-plugin"
PACKAGECONFIG[ruih] = "--enable-ruih-plugin,--disable-ruih-plugin"
PACKAGECONFIG[media-export] = "--enable-media-export-plugin,--disable-media-export-plugin"
PACKAGECONFIG[gst-launch] = "--enable-gst-launch-plugin,--disable-gst-launch-plugin"
PACKAGECONFIG[gtk+3] = ",--without-ui,gtk+3"

LIBV = "2.6"

do_install_append() {
       # Remove .la files for loadable modules
       rm -f ${D}/${libdir}/rygel-${LIBV}/engines/*.la
       rm -f ${D}/${libdir}/rygel-${LIBV}/plugins/*.la
       if [ -e ${D}${libdir}/systemd/user/rygel.service ]; then
               mkdir -p ${D}${systemd_unitdir}/system
               mv ${D}${libdir}/systemd/user/rygel.service ${D}${systemd_unitdir}/system
               rmdir ${D}${libdir}/systemd/user ${D}${libdir}/systemd
       fi
}

FILES_${PN} += "${libdir}/rygel-${LIBV}/engines ${datadir}/dbus-1 ${datadir}/icons"
FILES_${PN}-dbg += "${libdir}/rygel-${LIBV}/engines/.debug ${libdir}/rygel-${LIBV}/plugins/.debug"

PACKAGES += "${PN}-meta"
ALLOW_EMPTY_${PN}-meta = "1"

PACKAGES_DYNAMIC = "${PN}-plugin-*"

SYSTEMD_SERVICE_${PN} = "rygel.service"

python populate_packages_prepend () {
    rygel_libdir = d.expand('${libdir}/rygel-${LIBV}')
    postinst = d.getVar('plugin_postinst')
    pkgs = []

    pkgs += do_split_packages(d, oe.path.join(rygel_libdir, "plugins"), 'librygel-(.*)\.so$', d.expand('${PN}-plugin-%s'), 'Rygel plugin for %s', postinst=postinst, extra_depends=d.expand('${PN}'))
    pkgs += do_split_packages(d, oe.path.join(rygel_libdir, "plugins"), '(.*)\.plugin$', d.expand('${PN}-plugin-%s'), 'Rygel plugin for %s', postinst=postinst, extra_depends=d.expand('${PN}'))

    metapkg = d.getVar('PN') + '-meta'
    d.setVar('RDEPENDS_' + metapkg, ' '.join(pkgs))
}
