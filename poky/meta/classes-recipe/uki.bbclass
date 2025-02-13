# Unified kernel image (UKI) class
#
# This bbclass merges kernel, initrd etc as a UKI standard UEFI binary,
# to be loaded with UEFI firmware and systemd-boot on target HW.
# TPM PCR pre-calculation is not supported since systemd-measure tooling
# is meant to run on target, not in cross compile environment.
#
# See:
# https://www.freedesktop.org/software/systemd/man/latest/ukify.html
# https://uapi-group.org/specifications/specs/unified_kernel_image/
#
# The UKI contains:
#
#   - UEFI stub
#     The linux kernel can generate a UEFI stub, however the one from systemd-boot can fetch
#     the command line from a separate section of the EFI application, avoiding the need to
#     rebuild the kernel.
#   - kernel
#   - initramfs
#   - kernel command line
#   - uname -r kernel version
#   - /etc/os-release to create a boot menu with version details
#   - optionally secure boot signature(s)
#   - other metadata (e.g. TPM PCR measurements)
#
# Usage instructions:
#
#   - requires UEFI compatible firmware on target, e.g. qemuarm64-secureboot u-boot based
#     from meta-arm or qemux86 ovmf/edk2 based firmware for x86_64
#
#   - Distro/build config:
#
#     INIT_MANAGER = "systemd"
#     MACHINE_FEATURES:append = " efi"
#     EFI_PROVIDER = "systemd-boot"
#     INITRAMFS_IMAGE = "core-image-minimal-initramfs"
#
#   - image recipe:
#
#     inherit uki
#
#   - qemuboot/runqemu changes in image recipe or build config:
#
#     # Kernel command line must be inside the signed uki
#     QB_KERNEL_ROOT = ""
#     # kernel is in the uki image, not loaded separately
#     QB_DEFAULT_KERNEL = "none"
#
#   - for UEFI secure boot, systemd-boot and uki (including kernel) can
#     be signed but require sbsign-tool-native (recipe available from meta-secure-core,
#     see also qemuarm64-secureboot from meta-arm). Set variable
#     UKI_SB_KEY to path of private key and UKI_SB_CERT for certificate.
#     Note that systemd-boot also need to be signed with the same key.
#
#   - at runtime, UEFI firmware will load and boot systemd-boot which
#     creates a menu from all detected uki binaries. No need to manually
#     setup boot menu entries.
#
#   - see efi-uki-bootdisk.wks.in how to create ESP partition which hosts systemd-boot,
#     config file(s) for systemd-boot and the UKI binaries.
#

DEPENDS += "\
    os-release \
    systemd-boot \
    systemd-boot-native \
    virtual/cross-binutils \
    virtual/kernel \
"

inherit image-artifact-names
require ../conf/image-uefi.conf

INITRAMFS_IMAGE ?= "core-image-minimal-initramfs"

INITRD_ARCHIVE ?= "${INITRAMFS_IMAGE}-${MACHINE}.${INITRAMFS_FSTYPES}"

do_image_complete[depends] += "${INITRAMFS_IMAGE}:do_image_complete"

UKIFY_CMD ?= "ukify build"
UKI_CONFIG_FILE ?= "${UNPACKDIR}/uki.conf"
UKI_FILENAME ?= "uki.efi"
UKI_KERNEL_FILENAME ?= "${KERNEL_IMAGETYPE}"
UKI_CMDLINE ?= "rootwait root=LABEL=root console=${KERNEL_CONSOLE}"
# secure boot keys and cert, needs sbsign-tools-native (meta-secure-core)
#UKI_SB_KEY ?= ""
#UKI_SB_CERT ?= ""

IMAGE_EFI_BOOT_FILES ?= "${UKI_FILENAME};EFI/Linux/${UKI_FILENAME}"

do_uki[depends] += " \
                        systemd-boot:do_deploy \
                        virtual/kernel:do_deploy \
                     "
do_uki[depends] += "${@ '${INITRAMFS_IMAGE}:do_image_complete' if d.getVar('INITRAMFS_IMAGE') else ''}"

# ensure that the build directory is empty everytime we generate a newly-created uki
do_uki[cleandirs] = "${B}"
# influence the build directory at the start of the builds
do_uki[dirs] = "${B}"

# we want to allow specifying files in SRC_URI, such as for signing the UKI
python () {
    d.delVarFlag("do_fetch","noexec")
    d.delVarFlag("do_unpack","noexec")
}

# main task
python do_uki() {
    import glob
    import bb.process

    # base ukify command, can be extended if needed
    ukify_cmd = d.getVar('UKIFY_CMD')

    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')

    # architecture
    target_arch = d.getVar('EFI_ARCH')
    if target_arch:
        ukify_cmd += " --efi-arch %s" % (target_arch)

    # systemd stubs
    stub = "%s/linux%s.efi.stub" % (d.getVar('DEPLOY_DIR_IMAGE'), target_arch)
    if not os.path.exists(stub):
        bb.fatal(f"ERROR: cannot find {stub}.")
    ukify_cmd += " --stub %s" % (stub)

    # initrd
    initramfs_image = "%s" % (d.getVar('INITRD_ARCHIVE'))
    ukify_cmd += " --initrd=%s" % (os.path.join(deploy_dir_image, initramfs_image))

    # kernel
    kernel_filename = d.getVar('UKI_KERNEL_FILENAME') or None
    if kernel_filename:
        kernel = "%s/%s" % (deploy_dir_image, kernel_filename)
        if not os.path.exists(kernel):
            bb.fatal(f"ERROR: cannot find %s" % (kernel))
        ukify_cmd += " --linux=%s" % (kernel)
        # not always needed, ukify can detect version from kernel binary
        kernel_version = d.getVar('KERNEL_VERSION')
        if kernel_version:
            ukify_cmd += "--uname %s" % (kernel_version)
    else:
        bb.fatal("ERROR - UKI_KERNEL_FILENAME not set")

    # command line
    cmdline = d.getVar('UKI_CMDLINE')
    if cmdline:
        ukify_cmd += " --cmdline='%s'" % (cmdline)

    # dtb
    if d.getVar('KERNEL_DEVICETREE'):
        for dtb in d.getVar('KERNEL_DEVICETREE').split():
            dtb_path = "%s/%s" % (deploy_dir_image, dtb)
            if not os.path.exists(dtb_path):
                bb.fatal(f"ERROR: cannot find {dtb_path}.")
            ukify_cmd += " --devicetree %s" % (dtb_path)

    # custom config for ukify
    if os.path.exists(d.getVar('UKI_CONFIG_FILE')):
        ukify_cmd += " --config=%s" % (d.getVar('UKI_CONFIG_FILE'))

    # systemd tools
    ukify_cmd += " --tools=%s%s/lib/systemd/tools" % \
        (d.getVar("RECIPE_SYSROOT_NATIVE"), d.getVar("prefix"))

    # version
    ukify_cmd += " --os-release=@%s%s/lib/os-release" % \
        (d.getVar("RECIPE_SYSROOT"), d.getVar("prefix"))

    # TODO: tpm2 measure for secure boot, depends on systemd-native and TPM tooling
    # needed in systemd > 254 to fulfill ConditionSecurity=measured-uki
    # Requires TPM device on build host, thus not supported at build time.
    #ukify_cmd += " --measure"

    # securebooot signing, also for kernel
    key = d.getVar('UKI_SB_KEY')
    if key:
        ukify_cmd += " --sign-kernel --secureboot-private-key='%s'" % (key)
    cert = d.getVar('UKI_SB_CERT')
    if cert:
        ukify_cmd += " --secureboot-certificate='%s'" % (cert)

    # custom output UKI filename
    output = " --output=%s/%s" % (d.getVar('DEPLOY_DIR_IMAGE'), d.getVar('UKI_FILENAME'))
    ukify_cmd += " %s" % (output)

    # Run the ukify command
    bb.debug(2, "uki: running command: %s" % (ukify_cmd))
    out, err = bb.process.run(ukify_cmd, shell=True)
    bb.debug(2, "%s\n%s" % (out, err))
}
addtask uki after do_rootfs before do_deploy do_image_complete do_image_wic
