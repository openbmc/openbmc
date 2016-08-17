require gssdp.inc

inherit gtk-doc

PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk,gtk+3"

SRC_URI = "${GNOME_MIRROR}/${BPN}/0.14/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "b8658e480d047caf2d92baa4a51b9ae7"
SRC_URI[sha256sum] = "4c3ffa01435e84dc31c954e669e1ca0749b962f76a333e74f5c2cb0de5803a13"
