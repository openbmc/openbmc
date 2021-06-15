DESCRIPTION = "Gstreamer Daemon"
SUMMARY = "GStreamer framework for controlling audio and video streaming using TCP connection messages"
HOMEPAGE = "https://developer.ridgerun.com/wiki/index.php?title=Gstd-1.0"
SECTION = "multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad gstreamer1.0-rtsp-server json-glib libdaemon jansson"

SRCBRANCH ?= "master"
SRCREV = "a6621a5778b234651aa2adbbe304d906a3fa64d1"
SRC_URI = "git://git@github.com/RidgeRun/gstd-1.x.git;protocol=https;branch=${SRCBRANCH} \
           file://0001-gstd-yocto-compatibility.patch \
           "
S = "${WORKDIR}/git"

# Remove the +really when upstream version is > 1.0
PV = "1.0+really0.8.0"

inherit autotools pkgconfig gettext gtk-doc

do_install_append() {
        rm -fr ${D}${localstatedir}/run ${D}/run ${D}${bindir}/gst-client ${D}${bindir}/gstd-client
        if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
                install -d ${D}${sysconfdir}/tmpfiles.d
                echo "d /run/${BPN} - - - -" \
                > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
                echo "d /${localstatedir}/log/${BPN} 0755 root root -" \
                >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
        fi
        ln -sf gst-client-1.0 ${D}${bindir}/gst-client
        ln -sf gst-client-1.0 ${D}${bindir}/gstd-client
}
