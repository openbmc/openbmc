SUMMARY = "Postinstall scriptlets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

FILES_${PN}_append = " ${datadir}/postinst-intercepts/*"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${datadir}/postinst-intercepts
	install -m 755 ${COREBASE}/scripts/postinst-intercepts/postinst_intercept ${D}${datadir}/postinst-intercepts/
	install -m 755 ${COREBASE}/scripts/postinst-intercepts/update_font_cache ${D}${datadir}/postinst-intercepts/
	install -m 755 ${COREBASE}/scripts/postinst-intercepts/update_icon_cache ${D}${datadir}/postinst-intercepts/
	install -m 755 ${COREBASE}/scripts/postinst-intercepts/update_pixbuf_cache ${D}${datadir}/postinst-intercepts/
}

inherit nativesdk
INHIBIT_DEFAULT_DEPS = "1"
