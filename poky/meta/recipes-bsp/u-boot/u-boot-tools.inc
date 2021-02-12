SUMMARY = "U-Boot bootloader tools"
DEPENDS += "openssl"

PROVIDES = "${MLPREFIX}u-boot-mkimage ${MLPREFIX}u-boot-mkenvimage"
PROVIDES_class-native = "u-boot-mkimage-native u-boot-mkenvimage-native"

PACKAGES += "${PN}-mkimage ${PN}-mkenvimage"

# Required for backward compatibility with "u-boot-mkimage-xxx.bb"
RPROVIDES_${PN}-mkimage = "u-boot-mkimage"
RREPLACES_${PN}-mkimage = "u-boot-mkimage"
RCONFLICTS_${PN}-mkimage = "u-boot-mkimage"

EXTRA_OEMAKE_class-target = 'CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1'
EXTRA_OEMAKE_class-native = 'CC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1'
EXTRA_OEMAKE_class-nativesdk = 'CROSS_COMPILE="${HOST_PREFIX}" CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" STRIP=true V=1'

SED_CONFIG_EFI = '-e "s/CONFIG_EFI_LOADER=.*/# CONFIG_EFI_LOADER is not set/"'
SED_CONFIG_EFI_x86 = ''
SED_CONFIG_EFI_x86-64 = ''
SED_CONFIG_EFI_arm = ''
SED_CONFIG_EFI_armeb = ''
SED_CONFIG_EFI_aarch64 = ''

do_compile () {
	oe_runmake -C ${S} sandbox_defconfig O=${B}

	# Disable CONFIG_CMD_LICENSE, license.h is not used by tools and
	# generating it requires bin2header tool, which for target build
	# is built with target tools and thus cannot be executed on host.
	sed -i -e "s/CONFIG_CMD_LICENSE=.*/# CONFIG_CMD_LICENSE is not set/" ${SED_CONFIG_EFI} ${B}/.config

	oe_runmake -C ${S} cross_tools NO_SDL=1 O=${B}
}

do_install () {
	install -d ${D}${bindir}

	# mkimage
	install -m 0755 tools/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage

	# mkenvimage
	install -m 0755 tools/mkenvimage ${D}${bindir}/uboot-mkenvimage
	ln -sf uboot-mkenvimage ${D}${bindir}/mkenvimage

	# dumpimage
	install -m 0755 tools/dumpimage ${D}${bindir}/uboot-dumpimage
	ln -sf uboot-dumpimage ${D}${bindir}/dumpimage

	# fit_check_sign
	install -m 0755 tools/fit_check_sign ${D}${bindir}/uboot-fit_check_sign
	ln -sf uboot-fit_check_sign ${D}${bindir}/fit_check_sign
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = ""
FILES_${PN}-mkimage = "${bindir}/uboot-mkimage ${bindir}/mkimage ${bindir}/uboot-dumpimage ${bindir}/dumpimage ${bindir}/uboot-fit_check_sign ${bindir}/fit_check_sign"
FILES_${PN}-mkenvimage = "${bindir}/uboot-mkenvimage ${bindir}/mkenvimage"

RDEPENDS_${PN}-mkimage += "dtc"
RDEPENDS_${PN} += "${PN}-mkimage ${PN}-mkenvimage"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

