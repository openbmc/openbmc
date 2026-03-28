SUMMARY = "Sensors plugin for the Xfce Panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-sensors-plugin/start"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI += "file://0001-Do-not-check-for-sys-class-power_supply-we-are-cross.patch"
SRC_URI[sha256sum] = "840442b87fdddcd8595bd9f83ea8b81f771fe296bb9d2abf0e1979e208727ae9"

EXTRA_OECONF = " \
    --disable-procacpi \
    --disable-xnvctrl \
"

LDFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' -fuse-ld=bfd', '', d)}"

do_configure:prepend() {
    sed -i 's:LIBSENSORS_CFLAGS=.*:LIBSENSORS_CFLAGS=-I${STAGING_INCDIR}:g' ${S}/configure.ac
}

PACKAGECONFIG ??= "libnotify"
PACKAGECONFIG[libsensors] = "--enable-libsensors,--disable-libsensors, lmsensors"
PACKAGECONFIG[hddtemp]    = "--enable-hddtemp,--disable-hddtemp, hddtemp"
PACKAGECONFIG[netcat]     = "--enable-netcat --disable-pathchecks,--disable-netcat, netcat"
PACKAGECONFIG[libnotify]  = "--enable-notification,--disable-notification, libnotify"

FILES_SOLIBSDEV = "${libdir}/xfce4/modules/lib*${SOLIBSDEV}"
