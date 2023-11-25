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

SRC_URI[archive.sha256sum] = "cc63080eefa147a8472ab1a5ff087b97a27ab723a4ee005ed41e8c9dd7798e41"

GIR_MESON_OPTION = ""
VALA_MESON_OPTION = ""

FILES:${PN} += "${datadir}"

INSANE_SKIP:${PN} = "dev-deps"
