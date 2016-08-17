SUMMARY = "Tool for managing software RAID under Linux"
HOMEPAGE = "http://www.kernel.org/pub/linux/utils/raid/mdadm/"

# Some files are GPLv2+ while others are GPLv2.
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://mdmon.c;beginline=4;endline=18;md5=af7d8444d9c4d3e5c7caac0d9d34039d \
                    file://mdadm.h;beglinlne=4;endline=22;md5=462bc9936ac0d3da110191a3f9994161"


SRC_URI = "${KERNELORG_MIRROR}/linux/utils/raid/mdadm/${BPN}-${PV}.tar.xz \
           file://mdadm-3.2.2_fix_for_x32.patch \
           file://gcc-4.9.patch \
           file://mdadm-3.3.2_x32_abi_time_t.patch \
           file://0001-Fix-typo-in-comparision.patch \
           file://mdadm-fix-ptest-build-errors.patch \
           file://0001-util.c-include-poll.h-instead-of-sys-poll.h.patch \
           file://0001-mdadm.h-bswap-is-already-defined-in-uclibc.patch \
           file://0001-Fix-some-type-comparison-problems.patch \
           file://0001-Fix-the-path-of-corosync-and-dlm-header-files-check.patch \
           file://run-ptest \
	  "
SRC_URI[md5sum] = "04b8b21f637540350f8517c7e68d3c63"
SRC_URI[sha256sum] = "27d0be4627d38a12ddcd1c1c3721d649d4e89e1093914497e22b57245cda8808"

CFLAGS += "-fno-strict-aliasing"
inherit autotools-brokensep

EXTRA_OEMAKE = 'CHECK_RUN_DIR=0 CXFLAGS="${CFLAGS}"'
# PPC64 and MIPS64 uses long long for u64 in the kernel, but powerpc's asm/types.h
# prevents 64-bit userland from seeing this definition, instead defaulting
# to u64 == long in userspace. Define __SANE_USERSPACE_TYPES__ to get
# int-ll64.h included
CFLAGS_append_powerpc64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mips64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mips64n32 = ' -D__SANE_USERSPACE_TYPES__'

do_compile() {
	oe_runmake SYSROOT="${STAGING_DIR_TARGET}"
}

do_install() {
	export STRIP=""
	autotools_do_install
}

FILES_${PN} += "${base_libdir}/udev/rules.d/*.rules"

inherit ptest

do_compile_ptest() {
	oe_runmake test
}

do_install_ptest() {
	cp -a ${S}/tests ${D}${PTEST_PATH}/tests
	cp ${S}/test ${D}${PTEST_PATH}
	sed -e 's!sleep 0.*!sleep 1!g; s!/var/tmp!/!g' -i ${D}${PTEST_PATH}/test
	ln -s /sbin/mdadm ${D}${PTEST_PATH}/mdadm
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
