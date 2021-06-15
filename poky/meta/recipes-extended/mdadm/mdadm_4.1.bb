SUMMARY = "Tool for managing software RAID under Linux"
HOMEPAGE = "http://www.kernel.org/pub/linux/utils/raid/mdadm/"
DESCRIPTION = "mdadm is a Linux utility used to manage and monitor software RAID devices."

# Some files are GPLv2+ while others are GPLv2.
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://mdmon.c;beginline=4;endline=18;md5=af7d8444d9c4d3e5c7caac0d9d34039d \
                    file://mdadm.h;beglinlne=4;endline=22;md5=462bc9936ac0d3da110191a3f9994161"


SRC_URI = "${KERNELORG_MIRROR}/linux/utils/raid/mdadm/${BPN}-${PV}.tar.xz \
           file://run-ptest \
           file://mdadm-3.3.2_x32_abi_time_t.patch \
           file://mdadm-fix-ptest-build-errors.patch \
           file://0001-mdadm.h-Undefine-dprintf-before-redefining.patch \
           file://0001-Fix-the-path-of-corosync-and-dlm-header-files-check.patch \
           file://0001-Use-CC-to-check-for-implicit-fallthrough-warning-sup.patch \
           file://0001-Compute-abs-diff-in-a-standard-compliant-way.patch \
           file://0001-fix-gcc-8-format-truncation-warning.patch \
           file://debian-no-Werror.patch \
           file://0001-Revert-tests-wait-for-complete-rebuild-in-integrity-.patch \
           file://mdadm.init \
           file://0001-mdadm-add-option-y-for-use-syslog-to-recive-event-re.patch \
           file://include_sysmacros.patch \
           file://0001-mdadm-skip-test-11spare-migration.patch \
           "

SRC_URI[md5sum] = "51bf3651bd73a06c413a2f964f299598"
SRC_URI[sha256sum] = "ab7688842908d3583a704d491956f31324c3a5fc9f6a04653cb75d19f1934f4a"

inherit autotools-brokensep ptest systemd

SYSTEMD_SERVICE_${PN} = "mdmonitor.service"
SYSTEMD_AUTO_ENABLE = "disable"

CFLAGS_append_toolchain-clang = " -Wno-error=address-of-packed-member"

# PPC64 and MIPS64 uses long long for u64 in the kernel, but powerpc's asm/types.h
# prevents 64-bit userland from seeing this definition, instead defaulting
# to u64 == long in userspace. Define __SANE_USERSPACE_TYPES__ to get
# int-ll64.h included
CFLAGS_append_powerpc64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mipsarchn64 = ' -D__SANE_USERSPACE_TYPES__'
CFLAGS_append_mipsarchn32 = ' -D__SANE_USERSPACE_TYPES__'

EXTRA_OEMAKE = 'CHECK_RUN_DIR=0 CXFLAGS="${CFLAGS}" SYSTEMD_DIR=${systemd_unitdir}/system \
                BINDIR="${base_sbindir}" UDEVDIR="${nonarch_base_libdir}/udev"'

DEBUG_OPTIMIZATION_append = " -Wno-error"

do_compile() {
	oe_runmake SYSROOT="${STAGING_DIR_TARGET}"
}

do_install() {
	export STRIP=""
	autotools_do_install
}

do_install_append() {
        install -d ${D}/${sysconfdir}/
        install -m 644 ${S}/mdadm.conf-example ${D}${sysconfdir}/mdadm.conf
        install -d ${D}/${sysconfdir}/init.d
        install -m 755 ${WORKDIR}/mdadm.init ${D}${sysconfdir}/init.d/mdmonitor
}

do_install_append() {
        oe_runmake install-systemd DESTDIR=${D}
}

do_compile_ptest() {
	oe_runmake test
}

do_install_ptest() {
	cp -R --no-dereference --preserve=mode,links -v ${S}/tests ${D}${PTEST_PATH}/tests
	cp ${S}/test ${D}${PTEST_PATH}
	sed -e 's!sleep 0.*!sleep 1!g; s!/var/tmp!/mdadm-testing-dir!g' -i ${D}${PTEST_PATH}/test
	sed -e 's!/var/tmp!/mdadm-testing-dir!g' -i ${D}${PTEST_PATH}/tests/*
        sed -i -e '/echo -ne "$_script... "/d' \
               -e 's/echo "succeeded"/echo -e "PASS: $_script"/g' \
               -e '/save_log fail/N; /_fail=1/i\\t\t\techo -ne "FAIL: $_script"' \
               -e '/die "dmesg prints errors when testing $_basename!"/i\\t\t\t\techo -ne "FAIL: $_script" &&' \
               ${D}${PTEST_PATH}/test

        chmod +x ${D}${PTEST_PATH}/test

	ln -s ${base_sbindir}/mdadm ${D}${PTEST_PATH}/mdadm
	for prg in test_stripe swap_super raid6check
	do
		install -D -m 755 $prg ${D}${PTEST_PATH}/
	done
}

RDEPENDS_${PN}-ptest += "bash e2fsprogs-mke2fs"
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

FILES_${PN} += "${systemd_unitdir}/*"
