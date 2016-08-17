SUMMARY = "Sensors plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-sensors-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b94789bed9aec03b9656a9cc5398c706"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "33c2e343c1224d9c4ae757a70cbe08eb"
SRC_URI[sha256sum] = "235ef842bd45e701bceebb21a384ab09f21afceea8ed95f91bb4c6cf3abe1bc0"

EXTRA_OECONF = " \
    --disable-procacpi \
    --disable-sysfsacpi \
    --disable-xnvctrl \
"

do_configure_prepend() {
    sed -i 's:LIBSENSORS_CFLAGS=.*:LIBSENSORS_CFLAGS=-I${STAGING_INCDIR}:g' ${S}/configure.ac
}

PACKAGECONFIG ??= "libsensors"
PACKAGECONFIG[libsensors] = "--enable-libsensors,--disable-libsensors, lmsensors"
PACKAGECONFIG[hddtemp]    = "--enable-hddtemp,--disable-hddtemp, hddtemp"
PACKAGECONFIG[netcat]     = "--enable-netcat,--disable-netcat, netcat"
PACKAGECONFIG[libnotify]  = "--enable-notification,--disable-notification, libnotify"

FILES_SOLIBSDEV = "${libdir}/xfce4/modules/lib*${SOLIBSDEV}"
