DESCRIPTION = "CUPS backends, filters, and other software"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/openprinting/cups-filters"

LICENSE = "GPL-2.0-only & LGPL-2.0-only & MIT & GPL-2.0-or-later & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d77679ce6a2cc4d873d4ebbf2a401e6"

SECTION = "console/utils"

DEPENDS = "cups glib-2.0 glib-2.0-native dbus dbus-glib lcms poppler qpdf libpng libexif"
DEPENDS:class-native = "poppler-native glib-2.0-native dbus-native pkgconfig-native gettext-native libpng-native"

SRC_URI = "https://github.com/OpenPrinting/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://0001-use-noexcept-false-instead-of-throw-from-c-17-onward.patch"
SRC_URI[sha256sum] = "270a3752a960368aa99d431fb5d34f4039b2ac943c576d840612d1d8185c9bb9"

inherit autotools-brokensep gettext pkgconfig github-releases

EXTRA_OECONF += " --disable-ldap \
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

PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[tiff] = "--with-tiff,--without-tiff,tiff"

PACKAGECONFIG ??= "dbus ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)}"

PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,,dbus"

DIRFILES = "1"

PACKAGES =+ "\
	${PN}-gst \
        ${PN}-data \
        "

FILES:${PN}-gst = "\
	${libexecdir}/cups/filter/gsto* \
	"
RDEPENDS:${PN}-gst += "ghostscript"

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
RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "ghostscript"

do_install:append() {
	# remove braille dir
	rm -rf ${D}${datadir}/cups/braille

	# remove sysroot path contamination from pkgconfig file
	sed -i -e 's:${STAGING_DIR_TARGET}::' ${D}/${libdir}/pkgconfig/libcupsfilters.pc
}
