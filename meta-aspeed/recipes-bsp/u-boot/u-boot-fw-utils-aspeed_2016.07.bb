require u-boot-common-aspeed_${PV}.inc

SRC_URI += "file://default-gcc.patch"
SRC_URI += "file://fw_env.config"
SRC_URI += "file://alt_fw_env.config"

SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
DEPENDS += "mtd-utils"

PROVIDES += "u-boot-fw-utils"

INSANE_SKIP:${PN} = "already-stripped"
EXTRA_OEMAKE:class-target = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" V=1'
EXTRA_OEMAKE:class-cross = 'ARCH=${TARGET_ARCH} CC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'

inherit uboot-config

do_compile () {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake env
}

do_install () {
	install -d ${D}${base_sbindir}
	install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
	install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv

	install -d ${D}${sysconfdir}
	install -m 644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
	install -m 644 ${UNPACKDIR}/alt_fw_env.config ${D}${sysconfdir}/alt_fw_env.config
}

do_install:class-cross () {
	install -d ${D}${bindir_cross}
	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_DIRS:append:class-cross = " ${bindir_cross}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"

RDEPENDS:${PN} = "udev-aspeed-mtd-partitions"
