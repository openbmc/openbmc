SUMMARY = "A UPnP AV media server and renderer"
DESCRIPTION = "Rygel is a home media solution (UPnP AV MediaServer) that \
allow you to easily share audio, video and pictures to other devices. \
Additionally, media player software may use Rygel to become a MediaRenderer \
that may be controlled remotely by a UPnP or DLNA Controller."
HOMEPAGE = "http://live.gnome.org/Rygel"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libxml2 glib-2.0 gssdp gupnp gupnp-av gupnp-dlna gstreamer1.0 \
           gstreamer1.0-plugins-base libgee libsoup libmediaart-2.0 \
           libunistring sqlite3 intltool-native gst-editing-services"

RDEPENDS:${PN} = "gstreamer1.0-plugins-base-playback shared-mime-info"
RRECOMMENDS:${PN} = "rygel-plugin-media-export"

inherit gnomebase features_check vala gobject-introspection gettext systemd

# gobject-introspection is mandatory for libmediaart-2.0 and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data x11"

SRC_URI[archive.sha256sum] = "1133602d2e6710aade6b22aa98d93384c8d56396f344763413e96e0110b7d89f"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

EXTRA_OEMESON = "-Dengines=gstreamer -Dplugins=${@strip_comma('${RYGEL_PLUGINS}')}"
PACKAGECONFIG:append = "${@bb.utils.contains("DISTRO_FEATURES", "x11", " gtk+3", "", d)}"

PACKAGECONFIG ?= "external mpris ruih gst-launch media-export"

PACKAGECONFIG[external] = ""
PACKAGECONFIG[mpris] = ""
PACKAGECONFIG[ruih] = ""
PACKAGECONFIG[media-export] = ""
PACKAGECONFIG[gst-launch] = ""
PACKAGECONFIG[lms] = ""
PACKAGECONFIG[tracker3] = ""
PACKAGECONFIG[gtk+3] = ",-Dgtk=disabled,gtk+3"

RYGEL_PLUGINS = ""
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'external', ',external', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'mpris', ',mpris', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'ruih', ',ruih', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'gst-launch', ',gst-launch', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'lms', ',lms', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'media-export', ',media-export', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'tracker3', ',tracker3', '', d)}"
RYGEL_PLUGINS:append ="${@bb.utils.contains('PACKAGECONFIG', 'playbin', ',playbin', '', d)}"

LIBV = "2.8"

CFLAGS:append:toolchain-clang = " -Wno-error=int-conversion"

def strip_comma(s):
    return s.strip(',')

do_install:append() {
       # Remove .la files for loadable modules
       rm -f ${D}/${libdir}/rygel-${LIBV}/engines/*.la
       rm -f ${D}/${libdir}/rygel-${LIBV}/plugins/*.la
}

FILES:${PN} += "${libdir}/rygel-${LIBV}/engines ${systemd_user_unitdir} ${datadir}/dbus-1 ${datadir}/icons"
FILES:${PN}-dbg += "${libdir}/rygel-${LIBV}/engines/.debug ${libdir}/rygel-${LIBV}/plugins/.debug"

PACKAGES += "${PN}-meta"
ALLOW_EMPTY:${PN}-meta = "1"

PACKAGES_DYNAMIC = "${PN}-plugin-*"

python populate_packages:prepend () {
    rygel_libdir = d.expand('${libdir}/rygel-${LIBV}')
    postinst = d.getVar('plugin_postinst')
    pkgs = []

    pkgs += do_split_packages(d, oe.path.join(rygel_libdir, "plugins"), r'librygel-(.*)\.so$', d.expand('${PN}-plugin-%s'), 'Rygel plugin for %s', postinst=postinst, extra_depends=d.expand('${PN}'))
    pkgs += do_split_packages(d, oe.path.join(rygel_libdir, "plugins"), r'(.*)\.plugin$', d.expand('${PN}-plugin-%s'), 'Rygel plugin for %s', postinst=postinst, extra_depends=d.expand('${PN}'))

    metapkg = d.getVar('PN') + '-meta'
    d.setVar('RDEPENDS:' + metapkg, ' '.join(pkgs))
}
