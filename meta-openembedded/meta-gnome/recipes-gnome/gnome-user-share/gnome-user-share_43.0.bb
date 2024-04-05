SUMMARY = "This is gnome-user-share 43.0, a system for easy sharing of user files."
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"


DEPENDS = " \
    glib-2.0-native \
    glib-2.0 \
    systemd \
"

inherit gnomebase gsettings features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "git://gitlab.gnome.org/GNOME/gnome-user-share.git;protocol=https;branch=master"
SRCREV = "a0e790aa9494db9d1b1f48b4fc0d2f78e112044d"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= "modules"
PACKAGECONFIG[httpd] = "-Dhttpd=${sbindir}/httpd,,,apache2 mod-dnssd"
PACKAGECONFIG[modules] = "-Dmodules_path=${libexecdir}/apache2/modules"

FILES:${PN} += "${systemd_user_unitdir}"
