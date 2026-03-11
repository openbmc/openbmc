SUMMARY = "A simple GNOME application to access virtual machines."
SECTION = "network"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://copyright;md5=a65e9b0c9f78617732f09f68fc4ef79a"

GNOMEBASEBUILDCLASS = "meson"

# reason for opengl: spice-gtk requires opengl
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data opengl"

DEPENDS = " \
  appstream-native \
  glib-2.0 \
  desktop-file-utils-native \
  itstool-native \
  libarchive \
  libgudev \
  libhandy \
  libosinfo \
  libportal \
  libsecret \
  libsoup \
  libvirt-glib \
  mtools \
  spice-gtk \
  tinysparql \
  webkitgtk3 \
  appstream-glib-native \
  spice-protocol \
  yelp-tools \
"

RDEPENDS:${PN} = "glib-2.0-dev libvirt-virsh qemu-common qemu-system-x86-64 genisoimage"

inherit gnomebase gsettings pkgconfig mime-xdg gtk-icon-cache gobject-introspection vala features_check

SRC_URI[archive.sha256sum] = "d05f5f42568fafbf6d88771161b06ed5f739d43121278d418cae95c56e513ead"

GIR_MESON_OPTION = ""
VALA_MESON_OPTION = ""

FILES:${PN} += "${datadir}"

INSANE_SKIP:${PN} = "dev-deps"
