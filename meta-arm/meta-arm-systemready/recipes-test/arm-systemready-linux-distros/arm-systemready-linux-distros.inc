SUMMARY = "Arm SystemReady Linux distros installation"
DESCRIPTION = "Arm SystemReady Linux distro CD/DVD images and installation \
               target disk image"

DISTRO_UNATTENDED_INST_TESTS ?= "0"

require ${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "arm-systemready-linux-distros-unattended.inc", "", d)}

IMAGE_CLASSES:remove = "license_image create-spdx-image-3.0"
IMAGE_CLASSES:remove = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "", "testimage", d)}"
BUILDHISTORY_FEATURES:remove = "image"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_HOST = "aarch64-*"
PACKAGE_ARCH = "${TARGET_ARCH}"

inherit nopackages deploy rootfs-postcommands ${IMAGE_CLASSES}

do_configure[noexec] = "1"
do_compile[noexec] = "1"

ISO_IMAGE_NAME = "${PN}-${PV}"
IMAGE_LINK_NAME = "${PN}-${MACHINE}"

ARM_SYSTEMREADY_LINUX_DISTRO_ISO_IMAGE = \
    "${DEPLOY_DIR_IMAGE}/${ISO_IMAGE_NAME}.iso"
# Size of installation disk in MB
ARM_SYSTEMREADY_LINUX_DISTRO_INSTALL_SIZE ?= "4096"

S = "${WORKDIR}/sources-unpack"

do_image() {
    dd if=/dev/zero of=${WORKDIR}/${IMAGE_LINK_NAME}.wic \
        bs=1M count=${ARM_SYSTEMREADY_LINUX_DISTRO_INSTALL_SIZE} status=none
}

do_deploy() {
    # Deploy the iso and installation target disk image to the deploy folder
    install -m 644 ${UNPACKDIR}/${ISO_IMAGE_NAME}.iso ${DEPLOYDIR}
    install -m 644 ${WORKDIR}/${IMAGE_LINK_NAME}.wic ${DEPLOYDIR}
}

addtask image before do_install
addtask deploy after do_install before do_image_complete

# Post-process commands may write to IMGDEPLOYDIR
IMGDEPLOYDIR = "${DEPLOY_DIR_IMAGE}"

python do_image_complete() {
    # Run the image post-process commands
    from oe.utils import execute_pre_post_process
    post_process_cmds = d.getVar("IMAGE_POSTPROCESS_COMMAND")
    execute_pre_post_process(d, post_process_cmds)

    if d.getVar('DISTRO_UNATTENDED_INST_TESTS') == "1":
        # Ensure an empty rootfs manifest exists (required by testimage)
        fname = os.path.join(d.getVar('IMGDEPLOYDIR'), d.getVar('IMAGE_LINK_NAME') + ".manifest")
        open(fname, 'w').close()
}

do_image_complete[nostamp] = "1"
addtask image_complete after do_deploy before do_build

do_install[depends] += "arm-systemready-firmware:do_image_complete"
