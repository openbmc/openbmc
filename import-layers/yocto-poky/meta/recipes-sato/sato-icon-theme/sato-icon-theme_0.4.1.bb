SUMMARY = "Sato icon theme"
HOMEPAGE = "http://www.o-hand.com"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=56a830bbe6e4697fe6cbbae01bb7c2b2"
SECTION = "x11"

PR = "r6"

DEPENDS = "icon-naming-utils-native libxml-simple-perl-native"

SRC_URI = "http://pokylinux.org/releases/sato/${BPN}-${PV}.tar.gz \
           file://0001-Inherit-the-GNOME-icon-theme.patch"

SRC_URI[md5sum] = "86a847f3128a43a9cf23b7029a656f50"
SRC_URI[sha256sum] = "0b0a2807a6a96918ac799a86094ec3e8e2c892be0fd679a4232c2a77f2f61732"

inherit autotools pkgconfig allarch gtk-icon-cache perlnative

# The configure script uses pkg-config to find native binaries to execute, so
# tell it to use our pkg-config-native wrapper.
export PKG_CONFIG = "pkg-config-native"

FILES_${PN} += "${datadir}/icons/Sato"

do_install_append() {
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s preferences-system.png {}/apps/preferences-desktop.png \;
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s file-manager.png {}/apps/system-file-manager.png \;
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s ../apps/terminal.png {}/places/computer.png \;
}

# Explictly setting "Sato" as the default icon theme to avoid flickering from
# the desktop and settings daemon racing.  This shouldn't be done here but in the sato image
pkg_postinst_${PN} () {
    mkdir -p $D/etc/gtk-2.0

    grep -s -q -e ^gtk-icon-theme-name.*\"Sato\" $D/etc/gtk-2.0/gtkrc || \
        echo 'gtk-icon-theme-name = "Sato"' >> $D/etc/gtk-2.0/gtkrc
}
