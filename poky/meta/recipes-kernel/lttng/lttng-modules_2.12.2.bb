SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f882d431dc0f32f1f44c0707aa41128"

inherit module

include lttng-platforms.inc

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           file://BUILD_RUNTIME_BUG_ON-vs-gcc7.patch \
           file://0001-Kconfig-fix-dependency-issue-when-building-in-tree-w.patch \
           file://0002-fix-Move-mmutrace.h-into-the-mmu-sub-directory-v5.9.patch \
           file://0003-fix-KVM-x86-mmu-Make-kvm_mmu_page-definition-and-acc.patch \
           file://0004-fix-ext4-limit-the-length-of-per-inode-prealloc-list.patch \
           file://0005-fix-ext4-indicate-via-a-block-bitmap-read-is-prefetc.patch \
           file://0006-fix-removal-of-smp_-read_barrier_depends-v5.9.patch \
           file://0007-fix-writeback-Drop-I_DIRTY_TIME_EXPIRE-v5.9.patch \
           file://0008-fix-writeback-Fix-sync-livelock-due-to-b_dirty_time-.patch \
           file://0009-fix-version-ranges-for-ext4_discard_preallocations-a.patch \
           file://0010-Fix-system-call-filter-table.patch \
           "

SRC_URI[sha256sum] = "df50bc3bd58679705714f17721acf619a8b0cedc694f8a97052aa5099626feca"

export INSTALL_MOD_DIR="kernel/lttng-modules"

EXTRA_OEMAKE += "KERNELDIR='${STAGING_KERNEL_DIR}'"

do_install_append() {
	# Delete empty directories to avoid QA failures if no modules were built
	find ${D}/${nonarch_base_libdir} -depth -type d -empty -exec rmdir {} \;
}

python do_package_prepend() {
    if not os.path.exists(os.path.join(d.getVar('D'), d.getVar('nonarch_base_libdir')[1:], 'modules')):
        bb.warn("%s: no modules were created; this may be due to CONFIG_TRACEPOINTS not being enabled in your kernel." % d.getVar('PN'))
}

BBCLASSEXTEND = "devupstream:target"
LIC_FILES_CHKSUM_class-devupstream = "file://LICENSE;md5=3f882d431dc0f32f1f44c0707aa41128"
DEFAULT_PREFERENCE_class-devupstream = "-1"
SRC_URI_class-devupstream = "git://git.lttng.org/lttng-modules;branch=stable-2.12 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           file://BUILD_RUNTIME_BUG_ON-vs-gcc7.patch \
           "
SRCREV_class-devupstream = "ad594e3a953db1b0c3c059fde45b5a5494f6be78"
PV_class-devupstream = "2.12.2+git${SRCPV}"
S_class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
