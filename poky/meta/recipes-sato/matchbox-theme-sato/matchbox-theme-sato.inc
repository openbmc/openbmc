SUMMARY = "Matchbox window manager Sato themes"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=aae86bb34b0a83716ca09f4e783d6ba4"

DEPENDS = "matchbox-wm"
SECTION = "x11/wm"

inherit autotools pkgconfig features_check

FILES_${PN} += "${datadir}/themes"

# The matchbox-wm requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
