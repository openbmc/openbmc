SUMMARY = "Sensors plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-sensors-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b94789bed9aec03b9656a9cc5398c706"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "7327c4c316ebd5d93665e77b432b8d89"
SRC_URI[sha256sum] = "3dc6643d2c064b7718badff44b948f8d410f00f13db197820b26ae38045f5112"
SRC_URI += "file://0001-Do-not-check-for-sys-class-power_supply-we-are-cross.patch"

EXTRA_OECONF = " \
    --disable-procacpi \
    --disable-xnvctrl \
"

do_configure_prepend() {
    sed -i 's:LIBSENSORS_CFLAGS=.*:LIBSENSORS_CFLAGS=-I${STAGING_INCDIR}:g' ${S}/configure.ac
}

PACKAGECONFIG[libsensors] = "--enable-libsensors,--disable-libsensors, lmsensors"
PACKAGECONFIG[hddtemp]    = "--enable-hddtemp,--disable-hddtemp, hddtemp"
PACKAGECONFIG[netcat]     = "--enable-netcat,--disable-netcat, netcat"
PACKAGECONFIG[libnotify]  = "--enable-notification,--disable-notification, libnotify"

FILES_SOLIBSDEV = "${libdir}/xfce4/modules/lib*${SOLIBSDEV}"
