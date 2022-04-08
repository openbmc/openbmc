SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app mime-xdg

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "0eee869922ec00a253dafa446c2aad2a2f98e07e1db7262e8337ce9ec2dad969"

FILES:${PN} += "${datadir}/metainfo"
