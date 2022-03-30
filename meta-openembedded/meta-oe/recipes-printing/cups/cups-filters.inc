DESCRIPTION = "CUPS backends, filters, and other software"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/openprinting/cups-filters"

LICENSE = "GPL-2.0-only & LGPL-2.0-only & MIT & GPL-2.0-or-later & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d77679ce6a2cc4d873d4ebbf2a401e6"

SECTION = "console/utils"

DEPENDS = "cups glib-2.0 glib-2.0-native dbus dbus-glib lcms ghostscript poppler qpdf libpng"
DEPENDS:class-native = "poppler-native glib-2.0-native dbus-native pkgconfig-native gettext-native libpng-native"

SRC_URI = "http://openprinting.org/download/cups-filters/cups-filters-${PV}.tar.gz"

inherit autotools-brokensep gettext pkgconfig

EXTRA_OECONF += " --enable-ghostscript --disable-ldap \
                       --with-pdftops=hybrid --enable-imagefilters \
                       --enable-ghostscript --with-gs-path=${bindir}/gs \
                       --with-pdftops-path=${bindir}/gs \
                       --with-fontdir=${datadir}/fonts --with-rcdir=no \
                       --with-cups-rundir=${localstatedir}/run/cups \
                       --localstatedir=${localstatedir}/var \
                       --with-rcdir=no \
                       --without-php"

EXTRA_OECONF:class-native += " --with-pdftops=pdftops \
                                    --disable-avahi --disable-ghostscript \
                                    --disable-ldap \
                                    --with-png --without-jpeg --without-tiff"

BBCLASSEXTEND = "native"

PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[tiff] = "--with-tiff,--without-tiff,tiff"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)}"

PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"

DIRFILES = "1"

PACKAGES =+ "\
	${PN}-gst \
        ${PN}-data \
        "

FILES:${PN}-gst = "\
	${libexecdir}/cups/filter/gsto* \
	"

FILES:${PN}-data = "\
	${datadir}/cups/data \
	"

FILES:${PN}-dbg += "\
	${libexecdir}/cups/backend/.debug \
	${libexecdir}/cups/driver/.debug \
	${libexecdir}/cups/filter/.debug \
	"

FILES:${PN} += "\
        ${libexecdir}/cups \
        ${datadir}/ppd/ \
        ${datadir}/cups/charsets \
        ${datadir}/cups/drv \
        ${datadir}/cups/mime \
        ${datadir}/cups/ppdc \
        ${datadir}/cups/banners \
"

do_install:append() {
	# remove braille dir
	rm -rf ${D}${datadir}/cups/braille

	# remove sysroot path contamination from pkgconfig file
	sed -i -e 's:${STAGING_DIR_TARGET}::' ${D}/${libdir}/pkgconfig/libcupsfilters.pc
}

RDEPENDS:${PN} += "bash"
