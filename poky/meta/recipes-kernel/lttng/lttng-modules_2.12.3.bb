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
           file://0001-fix-btrfs-tracepoints-output-proper-root-owner-for-t.patch \
           file://0007-fix-objtool-Rename-frame.h-objtool.h-v5.10.patch \
           file://0009-fix-btrfs-make-ordered-extent-tracepoint-take-btrfs_.patch \
           file://0010-fix-ext4-fast-commit-recovery-path-v5.10.patch \
           file://0012-fix-kvm-x86-mmu-Add-TDP-MMU-PF-handler-v5.10.patch \
           file://0014-fix-tracepoint-Optimize-using-static_call-v5.10.patch \
           file://0016-fix-statedump-undefined-symbols-caused-by-incorrect-.patch \
           "

SRC_URI[sha256sum] = "673ef85c9f03e9b8fed10795e09d4e68add39404b70068d08b10f7b85754d7f0"

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
SRCREV_class-devupstream = "be71b60a327d7ad2588abc5cad2861177119972b"
PV_class-devupstream = "2.12.3+git${SRCPV}"
S_class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
