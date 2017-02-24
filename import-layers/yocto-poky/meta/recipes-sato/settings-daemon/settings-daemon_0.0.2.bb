SUMMARY = "Provides a bridge between gconf and xsettings"
HOMEPAGE = "http://svn.o-hand.com/view/matchbox/trunk/settings-daemon/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"
LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://xsettings-manager.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b \
                    file://xsettings-common.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b"
DEPENDS = "gconf glib-2.0 gtk+3"
SECTION = "x11"

# SRCREV tagged 0.0.2
SRCREV = "b2e5da502f8c5ff75e9e6da771372ef8e40fd9a2"
SRC_URI = "git://git.yoctoproject.org/xsettings-daemon \
           file://addsoundkeys.patch;apply=yes \
           file://70settings-daemon.sh \
           "
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gconf distro_features_check

FILES_${PN} = 	"${bindir}/* ${sysconfdir}"

# Requires gdk-x11-2.0 which is provided by gtk when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

do_install_append () {
	install -d ${D}/${sysconfdir}/X11/Xsession.d
	install -m 755 ${WORKDIR}/70settings-daemon.sh ${D}/${sysconfdir}/X11/Xsession.d/
}
