SUMMARY = "Sensors plugin for the Xfce Panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-sensors-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b94789bed9aec03b9656a9cc5398c706"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "0c74c3112c5e6e07647c116cd43ff5a7"
SRC_URI[sha256sum] = "7524ec4534de9ef7f676de2895a41bf70b73b94da5a27fd4a022b16eda56d0f4"

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
