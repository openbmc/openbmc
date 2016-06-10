#require recipes-bsp/u-boot/u-boot.inc

SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
SECTION = "bootloader"
DEPENDS = "mtd-utils"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb \
                    file://README;beginline=1;endline=22;md5=78b195c11cb6ef63e6985140db7d7bab"

# This revision corresponds to the tag "v2013.07"
# We use the revision in order to avoid having to fetch it from the
# repo during parse
# SRCREV = "62c175fbb8a0f9a926c88294ea9f7e88eb898f6c"
SRCREV="44f1262bdf39ad93032d39f17a298165372be82e"

PV = "v2013.07+git${SRCPV}"

UBRANCH = "v2013.07-aspeed-openbmc"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;branch=${UBRANCH};protocol=https"
S = "${WORKDIR}/git"

#INSANE_SKIP_${PN} = "already-stripped"
#EXTRA_OEMAKE_class-target = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'
#EXTRA_OEMAKE_class-cross = 'ARCH=${TARGET_ARCH} CC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'
EXTRA_OEMAKE = 'HOSTCC="${CC}" CC="${CC}" HOSTSTRIP="true"'

inherit uboot-config

do_compile () {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake env
}

do_install () {
	install -d ${D}${base_sbindir}
	install -d ${D}${sysconfdir}
	install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
	ln -sf fw_printenv ${D}${base_sbindir}/fw_setenv
	install -m 0644 ${S}/tools/env/fw_env.config ${D}${sysconfdir}/fw_env.config
}

#do_install_class-cross () {
#	install -d ${D}${bindir_cross}
#	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
#	install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
#}

#SYSROOT_PREPROCESS_FUNCS_class-cross = "uboot_fw_utils_cross"
#uboot_fw_utils_cross() {
#	sysroot_stage_dir ${D}${bindir_cross} ${SYSROOT_DESTDIR}${bindir_cross}
#}

PACKAGE_ARCH = "${MACHINE_ARCH}"
#BBCLASSEXTEND = "cross native"
