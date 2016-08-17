LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://avahi-common/address.h;endline=25;md5=b1d1d2cda1c07eb848ea7d6215712d9d \
                    file://avahi-core/dns.h;endline=23;md5=6fe82590b81aa0ddea5095b548e2fdcb \
                    file://avahi-daemon/main.c;endline=21;md5=9ee77368c5407af77caaef1b07285969 \
                    file://avahi-client/client.h;endline=23;md5=f4ac741a25c4f434039ba3e18c8674cf"

require avahi.inc

inherit python-dir pythonnative distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

PACKAGECONFIG ??= "dbus python"
PACKAGECONFIG[python] = "--enable-python,--disable-python,python-native python"

SRC_URI[md5sum] = "22b5e705d3eabb31d26f2e1e7b074013"
SRC_URI[sha256sum] = "d54991185d514a0aba54ebeb408d7575b60f5818a772e28fa0e18b98bc1db454"

DEPENDS += "avahi gtk+ libglade"

AVAHI_GTK = "--enable-gtk --disable-gtk3 --disable-pygtk"

S = "${WORKDIR}/avahi-${PV}"

PACKAGES = "${PN} ${PN}-utils ${PN}-dbg ${PN}-dev ${PN}-staticdev ${PN}-doc python-avahi avahi-discover"

FILES_${PN} = "${libdir}/libavahi-ui*.so.*"
FILES_${PN}-dev += "${libdir}/libavahi-ui${SOLIBSDEV}"
FILES_${PN}-staticdev += "${libdir}/libavahi-ui.a"

FILES_${PN}-utils = "${bindir}/b* ${datadir}/applications/b*"

FILES_python-avahi = "${PYTHON_SITEPACKAGES_DIR}/avahi ${PYTHON_SITEPACKAGES_DIR}/avahi_discover"
FILES_avahi-discover = "${datadir}/applications/avahi-discover.desktop \
                        ${datadir}/avahi/interfaces/avahi-discover* \
                        ${bindir}/avahi-discover-standalone \
                        ${datadir}/avahi/interfaces/avahi-discover.glade"

RDEPENDS_python-avahi = "python-core python-dbus"


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
	rm ${D}${datadir}/avahi/s*
	rm ${D}${datadir}/locale/ -rf
	rm ${D}${datadir}/dbus* -rf
	rm ${D}${mandir}/man1/a*
	rm ${D}${mandir}/man5 -rf
	rm ${D}${mandir}/man8 -rf
        rm ${D}${libdir}/girepository-1.0/ -rf
        rm ${D}${datadir}/gir-1.0/ -rf
}

