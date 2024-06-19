SUMMARY = "A simple GNOME application to access virtual machines."
SECTION = "network"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://copyright;md5=a65e9b0c9f78617732f09f68fc4ef79a"

GNOMEBASEBUILDCLASS = "meson"

REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"

DEPENDS = " \
  glib-2.0 \
  desktop-file-utils-native \
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
  tracker \
  webkitgtk3 \
  appstream-glib-native \
  spice-protocol \
  yelp-tools \
"

RDEPENDS:${PN} = "glib-2.0-dev libvirt-virsh qemu-common qemu-system-x86-64 genisoimage"

inherit gnomebase gsettings pkgconfig mime-xdg gtk-icon-cache gobject-introspection vala features_check

SRC_URI[archive.sha256sum] = "900c177f6762640370a6634cf9e7d3cd8207e498367a8a667a6b731b04116036"

GIR_MESON_OPTION = ""
VALA_MESON_OPTION = ""

CFLAGS += "-Wno-int-conversion"

FILES:${PN} += "${datadir}"

INSANE_SKIP:${PN} = "dev-deps"
