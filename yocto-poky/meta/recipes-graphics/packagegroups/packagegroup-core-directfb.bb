SUMMARY = "DirectFB without X11"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

TOUCH = ' ${@bb.utils.contains("MACHINE_FEATURES", "touchscreen", "tslib tslib-calibrate tslib-tests", "",d)}'

RDEPENDS_${PN} = " \
		directfb \
		directfb-examples \
		pango \
		pango-modules \
		fontconfig \
		${TOUCH} \
"
