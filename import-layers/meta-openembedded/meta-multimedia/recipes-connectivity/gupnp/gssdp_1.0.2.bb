require gssdp.inc

inherit gtk-doc

PACKAGECONFIG[gtk] = "--with-gtk,--without-gtk,gtk+3"

SRC_URI = "${GNOME_MIRROR}/${BPN}/1.0/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "b30c9a406853c6a3a012d151d6e7ad2c"
SRC_URI[sha256sum] = "a1e17c09c7e1a185b0bd84fd6ff3794045a3cd729b707c23e422ff66471535dc"
