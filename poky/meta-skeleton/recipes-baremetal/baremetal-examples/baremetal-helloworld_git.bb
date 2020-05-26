SUMMARY = "Baremetal examples to work with the several QEMU architectures supported on OpenEmbedded"
HOMEPAGE = "https://github.com/aehs29/baremetal-helloqemu"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39346640a23c701e4f459e05f56f4449"

SRCREV = "99f4fa4a3b266b42b52af302610b0f4f429ba5e3"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/aehs29/baremetal-helloqemu.git;protocol=https;branch=master"

S = "${WORKDIR}/git/"

# These examples are not meant to be built when using either musl or glibc
COMPATIBLE_HOST_libc-musl_class-target = "null"
COMPATIBLE_HOST_libc-glibc_class-target = "null"

# This will be translated automatically to the architecture and
# machine that QEMU uses on OE, e.g. -machine virt -cpu cortex-a57
# but the examples can also be run on other architectures/machines
# such as vexpress-a15 by overriding the setting on the machine.conf
COMPATIBLE_MACHINE = "qemuarmv5|qemuarm|qemuarm64"

BAREMETAL_QEMUARCH ?= ""
BAREMETAL_QEMUARCH_qemuarmv5 = "versatile"
BAREMETAL_QEMUARCH_qemuarm = "arm"
BAREMETAL_QEMUARCH_qemuarm64 = "aarch64"


EXTRA_OEMAKE_append = " QEMUARCH=${BAREMETAL_QEMUARCH} V=1"

do_install(){
    install -d ${D}/${datadir}
    install -m 755 ${B}build/hello_baremetal_${BAREMETAL_QEMUARCH}.bin ${D}/${datadir}/hello_baremetal_${MACHINE}.bin
    install -m 755 ${B}build/hello_baremetal_${BAREMETAL_QEMUARCH}.elf ${D}/${datadir}/hello_baremetal_${MACHINE}.elf
}

# Borrowed from meta-freertos
inherit rootfs-postcommands
inherit deploy
do_deploy[dirs] = "${DEPLOYDIR} ${DEPLOY_DIR_IMAGE}"
do_rootfs[dirs] = "${DEPLOYDIR} ${DEPLOY_DIR_IMAGE}"
DEPLOYDIR = "${IMGDEPLOYDIR}"
IMAGE_LINK_NAME ?= "baremetal-helloworld-image-${MACHINE}"
IMAGE_NAME_SUFFIX ?= ""

do_deploy(){
    install ${D}/${datadir}/hello_baremetal_${MACHINE}.bin ${DEPLOYDIR}/${IMAGE_LINK_NAME}.bin
    install ${D}/${datadir}/hello_baremetal_${MACHINE}.elf ${DEPLOYDIR}/${IMAGE_LINK_NAME}.elf
}

do_image(){
    :
}

FILES_${PN} += " \
    ${datadir}/hello_baremetal_${MACHINE}.bin \
    ${datadir}/hello_baremetal_${MACHINE}.elf \
"

python do_rootfs(){
    from oe.utils import execute_pre_post_process
    from pathlib import Path

    # Write empty manifest testdate file
    deploy_dir = d.getVar('DEPLOYDIR')
    link_name = d.getVar('IMAGE_LINK_NAME')
    manifest_name = d.getVar('IMAGE_MANIFEST')

    Path(manifest_name).touch()
    if os.path.exists(manifest_name) and link_name:
        manifest_link = deploy_dir + "/" + link_name + ".manifest"
        if os.path.lexists(manifest_link):
            os.remove(manifest_link)
        os.symlink(os.path.basename(manifest_name), manifest_link)
    execute_pre_post_process(d, d.getVar('ROOTFS_POSTPROCESS_COMMAND'))
}

# QEMU generic FreeRTOS parameters
QB_DEFAULT_KERNEL = "${IMAGE_LINK_NAME}.bin"
QB_MEM = "-m 256"
QB_OPT_APPEND = "-nographic"
QB_DEFAULT_FSTYPE = "bin"
QB_DTB = ""

# This next part is necessary to trick the build system into thinking
# its building an image recipe so it generates the qemuboot.conf
addtask do_deploy after do_write_qemuboot_conf before do_build
addtask do_rootfs before do_deploy after do_install
addtask do_image after do_rootfs before do_build
inherit qemuboot

# Based on image.bbclass to make sure we build qemu
python(){
    # do_addto_recipe_sysroot doesnt exist for all recipes, but we need it to have
    # /usr/bin on recipe-sysroot (qemu) populated
    def extraimage_getdepends(task):
        deps = ""
        for dep in (d.getVar('EXTRA_IMAGEDEPENDS') or "").split():
        # Make sure we only add it for qemu
            if 'qemu' in dep:
                deps += " %s:%s" % (dep, task)
        return deps
    d.appendVarFlag('do_image', 'depends', extraimage_getdepends('do_addto_recipe_sysroot'))
    d.appendVarFlag('do_image', 'depends', extraimage_getdepends('do_populate_sysroot'))
}
