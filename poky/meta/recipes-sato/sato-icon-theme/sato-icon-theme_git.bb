SUMMARY = "Sato icon theme"
HOMEPAGE = "https://www.yoctoproject.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=56a830bbe6e4697fe6cbbae01bb7c2b2"
SECTION = "x11"

DEPENDS = "icon-naming-utils-native libxml-simple-perl-native"

SRC_URI = "git://git.yoctoproject.org/sato-icon-theme.git;protocol=https;branch=master"
SRCREV = "d23f04ecb0328f655bf195df8eb04c1b734d53a9"
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig allarch gtk-icon-cache perlnative

# The configure script uses pkg-config to find native binaries to execute, so
# tell it to use our pkg-config-native wrapper.
export PKG_CONFIG = "pkg-config-native"

FILES:${PN} += "${datadir}/icons/Sato"

do_install:append() {
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s preferences-system.png {}/apps/preferences-desktop.png \;
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s file-manager.png {}/apps/system-file-manager.png \;
	find ${D}${datadir}/icons/Sato/ -maxdepth 1 -type d -exec ln -s ../apps/terminal.png {}/places/computer.png \;
}
