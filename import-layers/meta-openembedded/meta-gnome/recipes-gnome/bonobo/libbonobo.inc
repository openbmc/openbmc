DESCRIPTION = "Bonobo CORBA interfaces library"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"
SECTION = "x11/gnome/libs"
DEPENDS = "glib-2.0 orbit2 intltool-native libxml2 dbus dbus-glib"

inherit gnomebase gtk-doc lib_package

ORBIT_IDL_SRC = "${STAGING_BINDIR_NATIVE}/orbit-idl-2"

RDEPENDS_${PN}-bin = "${PN} perl"
FILES_${PN} += "${libdir}/orbit-2.0/*.so ${libdir}/bonobo/monikers/*.so"
FILES_${PN}-dbg += "${libdir}/bonobo/monikers/.debug \
    ${libdir}/bonobo-2.0/samples/.debug ${libdir}/orbit-2.0/.debug"
FILES_${PN}-dev += "${libdir}/orbit-2.0/*.la ${libdir}/bonobo/monikers/*.la ${libdir}/bonobo-2.0/samples"
FILES_${PN}-staticdev += "${libdir}/orbit-2.0/*.a ${libdir}/bonobo/monikers/*.a"

LEAD_SONAME = "libbonobo-2.so"

PARALLEL_MAKE = ""

do_compile() {
    oe_runmake ORBIT_IDL="${ORBIT_IDL_SRC}"
}

do_install() {
    oe_runmake ORBIT_IDL="${ORBIT_IDL_SRC}" DESTDIR="${D}" install
}
