SUMMARY = "Provides a bridge between gconf and xsettings"
HOMEPAGE = "https://git.yoctoproject.org/cgit/cgit.cgi/xsettings-daemon/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://xsettings-manager.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b \
                    file://xsettings-common.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b"
DEPENDS = "gconf glib-2.0 gtk+3"
SECTION = "x11"

PV .= "+git"
# SRCREV tagged 0.0.2 + one patch
SRCREV = "df669c6579a6ac7e1ef56be66617f35ae7d33d68"
SRC_URI = "git://git.yoctoproject.org/xsettings-daemon;branch=master;protocol=https \
           file://70settings-daemon.sh \
           "
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gconf features_check

FILES:${PN} = "${bindir}/* ${sysconfdir}"

# Requires gdk-x11-2.0 which is provided by gtk when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

do_install:append () {
	install -d ${D}/${sysconfdir}/X11/Xsession.d
	install -m 755 ${UNPACKDIR}/70settings-daemon.sh ${D}/${sysconfdir}/X11/Xsession.d/
}
