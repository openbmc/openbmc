require u-boot-common-aspeed-sdk_${PV}.inc
require recipes-bsp/u-boot/u-boot-configure.inc

SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
DEPENDS += "mtd-utils"

PROVIDES += "u-boot-fw-utils"
RPROVIDES:${PN} += "u-boot-fw-utils"

# The 32MB NOR and 64MB NOR layouts use the same configuration
SRC_URI += "file://fw_env_flash_nor.config"
SRC_URI += "file://fw_env_ast2600_mmc.config"

ENV_CONFIG_FILE = "fw_env_flash_nor.config"
ENV_CONFIG_FILE:df-phosphor-mmc = "fw_env_ast2600_mmc.config"

INSANE_SKIP:${PN} = "already-stripped"
EXTRA_OEMAKE:class-target = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" V=1'
EXTRA_OEMAKE:class-cross = 'HOSTCC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'

inherit uboot-config

do_compile () {
	oe_runmake envtools
}

do_install () {
	install -d ${D}${base_sbindir}
	install -m 755 ${B}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
	ln -sf fw_printenv ${D}${base_sbindir}/fw_setenv

	install -d ${D}${sysconfdir}
	install -m 644 ${UNPACKDIR}/${ENV_CONFIG_FILE} ${D}${sysconfdir}/fw_env.config
}

do_install:class-cross () {
	install -d ${D}${bindir_cross}
	install -m 755 ${B}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
	ln -sf fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_DIRS:append:class-cross = " ${bindir_cross}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"
