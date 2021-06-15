require u-boot-common-nuvoton.inc

SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
DEPENDS = "mtd-utils bison-native"

PROVIDES += "u-boot-fw-utils"

INSANE_SKIP_${PN} = "already-stripped"

EXTRA_OEMAKE_class-target = 'CROSS_COMPILE="${TARGET_PREFIX}" HOSTCC="${BUILD_CC} ${BUILD_FLAGS} ${BUILD_LDFLAGS}" CC="${CC} ${CFLAGS} ${LDFLAGS}" STRIP=true V=1'
EXTRA_OEMAKE_class-cross = 'ARCH=${TARGET_ARCH} CC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'

inherit uboot-config

do_compile () {
  oe_runmake ${UBOOT_MACHINE}
  oe_runmake envtools
}

do_install () {
  install -d ${D}${base_sbindir}
  install -d ${D}${sysconfdir}
  install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
  install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
  install -m 0644 ${S}/tools/env/fw_env.config ${D}${sysconfdir}/fw_env.config
}

do_install_class-cross () {
  install -d ${D}${bindir_cross}
  install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
  install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_PREPROCESS_FUNCS_class-cross = "uboot_fw_utils_cross"
uboot_fw_utils_cross() {
	sysroot_stage_dir ${D}${bindir_cross} ${SYSROOT_DESTDIR}${bindir_cross}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"
