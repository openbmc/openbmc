SUMMARY = "Tool for managing software RAID under Linux"
HOMEPAGE = "http://www.kernel.org/pub/linux/utils/raid/mdadm/"

# Some files are GPLv2+ while others are GPLv2.
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://mdmon.c;beginline=4;endline=18;md5=af7d8444d9c4d3e5c7caac0d9d34039d \
                    file://mdadm.h;beglinlne=4;endline=22;md5=462bc9936ac0d3da110191a3f9994161"


SRC_URI = "${KERNELORG_MIRROR}/linux/utils/raid/mdadm/${BPN}-${PV}.tar.xz \
           file://gcc-4.9.patch \
           file://mdadm-3.3.2_x32_abi_time_t.patch \
           file://mdadm-fix-ptest-build-errors.patch \
           file://0001-Fix-the-path-of-corosync-and-dlm-header-files-check.patch \
           file://run-ptest \
           file://0001-mdadm.h-Undefine-dprintf-before-redefining.patch \
           file://0001-include-sys-sysmacros.h-for-major-minor-defintions.patch \
           file://0001-mdadm-Add-Wimplicit-fallthrough-0-in-Makefile.patch \
           file://0002-mdadm-Specify-enough-length-when-write-to-buffer.patch \
           file://0003-Replace-snprintf-with-strncpy-at-some-places-to-avoi.patch \
           file://0004-mdadm-Forced-type-conversion-to-avoid-truncation.patch \
           file://0005-Add-a-comment-to-indicate-valid-fallthrough.patch \
           file://0001-Use-CC-to-check-for-implicit-fallthrough-warning-sup.patch \
           "
SRC_URI[md5sum] = "2cb4feffea9167ba71b5f346a0c0a40d"
SRC_URI[sha256sum] = "1d6ae7f24ced3a0fa7b5613b32f4a589bb4881e3946a5a2c3724056254ada3a9"

CFLAGS += "-fno-strict-aliasing"
inherit autotools-brokensep

EXTRA_OEMAKE = 'CHECK_RUN_DIR=0 CXFLAGS="${CFLAGS}"'
# PPC64 and MIPS64 uses long long for u64 in the kernel, but powerpc's asm/types.h
# prevents 64-bit userland from seeing this definition, instead defaulting
# to u64 == long in userspace. Define __SANE_USERSPACE_TYPES__ to get
# int-ll64.h included
CFLAGS_append_powerpc64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mipsarchn64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mipsarchn32 = ' -D__SANE_USERSPACE_TYPES__'

do_compile() {
	# Point to right sbindir
	sed -i -e "s;BINDIR  = /sbin;BINDIR = $base_sbindir;" ${S}/Makefile
	oe_runmake SYSROOT="${STAGING_DIR_TARGET}"
}

do_install() {
	export STRIP=""
	autotools_do_install
}

inherit ptest

do_compile_ptest() {
	oe_runmake test
}

do_install_ptest() {
	cp -R --no-dereference --preserve=mode,links -v ${S}/tests ${D}${PTEST_PATH}/tests
	cp ${S}/test ${D}${PTEST_PATH}
	sed -e 's!sleep 0.*!sleep 1!g; s!/var/tmp!/!g' -i ${D}${PTEST_PATH}/test
	ln -s ${base_sbindir}/mdadm ${D}${PTEST_PATH}/mdadm
	for prg in test_stripe swap_super raid6check
	do
		install -D -m 755 $prg ${D}${PTEST_PATH}/
	done
}
RDEPENDS_${PN}-ptest += "bash"
RRECOMMENDS_${PN}-ptest += " \
    coreutils \
    util-linux \
    kernel-module-loop \
    kernel-module-linear \
    kernel-module-raid0 \
    kernel-module-raid1 \
    kernel-module-raid10 \
    kernel-module-raid456 \
"
