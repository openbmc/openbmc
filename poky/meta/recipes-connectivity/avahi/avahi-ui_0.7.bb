require avahi.inc

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "avahi"

AVAHI_GTK = "gtk3"

S = "${WORKDIR}/avahi-${PV}"

PACKAGES += "${PN}-utils avahi-discover"

FILES_${PN} = "${libdir}/libavahi-ui*.so.*"
FILES_${PN}-utils = "${bindir}/b* ${datadir}/applications/b*"
FILES_avahi-discover = "${datadir}/applications/avahi-discover.desktop \
                        ${datadir}/avahi/interfaces/avahi-discover.ui \
                        ${bindir}/avahi-discover-standalone \
                        "

do_install_append () {
	rm ${D}${sysconfdir} -rf
	rm ${D}${base_libdir} -rf
	rm ${D}${systemd_unitdir} -rf
	# The ${systemd_unitdir} is /lib/systemd, so we need rmdir /lib,
	# but not ${base_libdir} here. And the /lib may not exist
	# whithout systemd.
	[ ! -d ${D}/lib ] || rmdir ${D}/lib --ignore-fail-on-non-empty
	rm ${D}${bindir}/avahi-b*
	rm ${D}${bindir}/avahi-p*
	rm ${D}${bindir}/avahi-r*
	rm ${D}${bindir}/avahi-s*
	rm ${D}${includedir}/avahi-c* -rf
	rm ${D}${includedir}/avahi-g* -rf
	rm ${D}${libdir}/libavahi-c*
	rm ${D}${libdir}/libavahi-g*
	rm ${D}${libdir}/pkgconfig/avahi-c*
	rm ${D}${libdir}/pkgconfig/avahi-g*
	rm ${D}${sbindir} -rf
	rm ${D}${datadir}/avahi/a*
	rm ${D}${datadir}/locale/ -rf
	rm ${D}${datadir}/dbus* -rf
	rm ${D}${mandir}/man1/a*
	rm ${D}${mandir}/man5 -rf
	rm ${D}${mandir}/man8 -rf
        rm ${D}${libdir}/girepository-1.0/ -rf
        rm ${D}${datadir}/gir-1.0/ -rf
}
