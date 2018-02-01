require kexec-tools.inc
export LDFLAGS = "-L${STAGING_LIBDIR}"
EXTRA_OECONF = " --with-zlib=yes"

SRC_URI += "${KERNELORG_MIRROR}/linux/utils/kernel/kexec/kexec-tools-${PV}.tar.gz \
            file://0002-powerpc-change-the-memory-size-limit.patch \
            file://0001-purgatory-Pass-r-directly-to-linker.patch \
            file://0001-vmcore-dmesg-Define-_GNU_SOURCE.patch \
            file://0001-kexec-exntend-the-semantics-of-kexec_iomem_for_each_.patch \
            file://0002-kexec-generalize-and-rename-get_kernel_stext_sym.patch \
            file://0003-arm64-identify-PHYS_OFFSET-correctly.patch \
            file://0004-arm64-kdump-identify-memory-regions.patch \
            file://0005-arm64-kdump-add-elf-core-header-segment.patch \
            file://0006-arm64-kdump-set-up-kernel-image-segment.patch \
            file://0007-arm64-kdump-set-up-other-segments.patch \
            file://0008-arm64-kdump-add-DT-properties-to-crash-dump-kernel-s.patch \
            file://0009-arm64-kdump-Add-support-for-binary-image-files.patch \
            file://0010-kexec-ARM-Fix-add_buffer_phys_virt-align-issue.patch \
            file://0001-x86-x86_64-Fix-format-warning-with-die.patch \
            file://0002-ppc-Fix-format-warning-with-die.patch \
            file://kexec-x32.patch \
         "

SRC_URI[md5sum] = "b2b2c5e6b29d467d6e99d587fb6b7cf5"
SRC_URI[sha256sum] = "b3e69519d2acced256843b1e8f1ecfa00d9b54fa07449ed78f05b9193f239370"

PACKAGES =+ "kexec kdump vmcore-dmesg"

ALLOW_EMPTY_${PN} = "1"
RRECOMMENDS_${PN} = "kexec kdump vmcore-dmesg"

FILES_kexec = "${sbindir}/kexec"
FILES_kdump = "${sbindir}/kdump ${sysconfdir}/init.d/kdump \
               ${sysconfdir}/sysconfig/kdump.conf"
FILES_vmcore-dmesg = "${sbindir}/vmcore-dmesg"

inherit update-rc.d

INITSCRIPT_PACKAGES = "kdump"
INITSCRIPT_NAME_kdump = "kdump"
INITSCRIPT_PARAMS_kdump = "start 56 2 3 4 5 . stop 56 0 1 6 ."

do_install_append () {
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/kdump ${D}${sysconfdir}/init.d/kdump
        install -d ${D}${sysconfdir}/sysconfig
        install -m 0644 ${WORKDIR}/kdump.conf ${D}${sysconfdir}/sysconfig
}
