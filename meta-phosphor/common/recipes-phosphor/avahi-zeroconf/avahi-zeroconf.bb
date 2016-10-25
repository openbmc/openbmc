inherit obmc-phosphor-license
inherit pkgconfig
#inherit systemd

SRC_URI = "file://start_avahi_zeroconf.py \
           file://avahi-zeroconf.ini"

SYSTEMD_SERVICE_${PN} += "avahi-zeroconf.service"
DEPENDS = "avahi"

do_install_append() {
        install -d ${D}/${sbindir}
        install ${WORKDIR}/avahi-zeroconf.ini ${D}/${sysconfdir}
        install ${WORKDIR}/start_avahi_zeroconf.py ${D}/${sbindir}
}

