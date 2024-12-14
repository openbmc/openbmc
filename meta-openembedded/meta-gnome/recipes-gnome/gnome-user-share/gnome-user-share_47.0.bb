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
SRC_URI += "file://0001-build-Define-list-of-libc-feature-test-macros.patch"
SRCREV = "77ecbaddab0ed2121859926acbfccc9cecdee0db"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= "modules"
PACKAGECONFIG[httpd] = "-Dhttpd=${sbindir}/httpd,,,apache2 mod-dnssd"
PACKAGECONFIG[modules] = "-Dmodules_path=${libexecdir}/apache2/modules"

FILES:${PN} += "${systemd_user_unitdir}"
