DESCRIPTION="Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS="glib-2.0 gtk+3 libxfce4util libxfce4ui garcon dbus-glib xfconf"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "6b6cf080c891f7945953f8084a901552"
SRC_URI[sha256sum] = "c0eb3b29eba3cfa9175ed35174e83a21faa2a2423ddb79501fe8846cc430e3ae"

FILES_${PN} += "${datadir}/appdata"
