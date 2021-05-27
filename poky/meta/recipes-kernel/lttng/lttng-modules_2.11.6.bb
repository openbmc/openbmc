SECTION = "devel"
SUMMARY = "Linux Trace Toolkit KERNEL MODULE"
DESCRIPTION = "The lttng-modules 2.0 package contains the kernel tracer modules"
HOMEPAGE = "https://lttng.org/"
LICENSE = "LGPLv2.1 & GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f882d431dc0f32f1f44c0707aa41128"

inherit module

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|aarch64|mips|nios2|arm|riscv).*-linux'

SRC_URI = "https://lttng.org/files/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           file://BUILD_RUNTIME_BUG_ON-vs-gcc7.patch \
           file://0001-fix-strncpy-equals-destination-size-warning.patch \
           file://0002-fix-objtool-Rename-frame.h-objtool.h-v5.10.patch \
           file://0003-fix-btrfs-tracepoints-output-proper-root-owner-for-t.patch \
           file://0004-fix-btrfs-make-ordered-extent-tracepoint-take-btrfs_.patch \
           file://0005-fix-ext4-fast-commit-recovery-path-v5.10.patch \
           file://0006-fix-KVM-x86-Add-intr-vectoring-info-and-error-code-t.patch \
           file://0007-fix-kvm-x86-mmu-Add-TDP-MMU-PF-handler-v5.10.patch \
           file://0008-fix-KVM-x86-mmu-Return-unique-RET_PF_-values-if-the-.patch \
           file://0009-fix-tracepoint-Optimize-using-static_call-v5.10.patch \
           file://0010-fix-include-order-for-older-kernels.patch \
           file://0011-Add-release-maintainer-script.patch \
           file://0012-Improve-the-release-script.patch \
           file://0013-fix-backport-of-fix-ext4-fast-commit-recovery-path-v.patch \
           file://0014-Revert-fix-include-order-for-older-kernels.patch \
           file://0015-fix-backport-of-fix-tracepoint-Optimize-using-static.patch \
           file://0016-fix-adjust-version-range-for-trace_find_free_extent.patch \
           "

SRC_URI[md5sum] = "8ef09fdfcdec669d33f7fc1c1c80f2c4"
SRC_URI[sha256sum] = "23372811cdcd2ac28ba8c9d09484ed5f9238cfbd0043f8c663ff3875ba9c8566"

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
SRC_URI_class-devupstream = "git://git.lttng.org/lttng-modules;branch=stable-2.11 \
           file://Makefile-Do-not-fail-if-CONFIG_TRACEPOINTS-is-not-en.patch \
           file://BUILD_RUNTIME_BUG_ON-vs-gcc7.patch \
           "
SRCREV_class-devupstream = "17c413953603f063f2a9d6c3788bec914ce6f955"
PV_class-devupstream = "2.11.2+git${SRCPV}"
S_class-devupstream = "${WORKDIR}/git"
SRCREV_FORMAT ?= "lttng_git"
