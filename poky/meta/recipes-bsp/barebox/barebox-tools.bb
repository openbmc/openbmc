SUMMARY = "barebox bootloader tools"

require barebox-common.inc

LICENSE = "GPL-2.0-only"

DEPENDS = "bison-native flex-native libusb1"

S = "${WORKDIR}/barebox-${PV}"
B = "${WORKDIR}/build"

inherit pkgconfig

EXTRA_OEMAKE = " \
  ARCH=sandbox \
  CROSS_COMPILE=${TARGET_PREFIX} -C ${S} O=${B} \
  CROSS_PKG_CONFIG=pkg-config \
  CC='${CC}' \
  LD='${LD}' \
  "

do_compile:class-target () {
    export userccflags="${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
    export userldflags="${TARGET_LDFLAGS}${TOOLCHAIN_OPTIONS}"
    oe_runmake targettools_defconfig
    oe_runmake scripts
}

do_compile:class-native () {
    oe_runmake hosttools_defconfig
    oe_runmake scripts
}

BAREBOX_TOOLS = " \
         bareboxenv \
         bareboxcrc32 \
         kernel-install \
         bareboximd \
         omap3-usb-loader \
         omap4_usbboot \
         imx/imx-usb-loader \
         "

BAREBOX_TOOLS_SUFFIX = ""
BAREBOX_TOOLS_SUFFIX:class-target = "-target"

do_install () {
	install -d ${D}${bindir}

	for tool in ${BAREBOX_TOOLS}; do
		install -m 0755 scripts/${tool}${BAREBOX_TOOLS_SUFFIX} ${D}${bindir}/${tool##*/}
	done
}

BBCLASSEXTEND = "native nativesdk"
