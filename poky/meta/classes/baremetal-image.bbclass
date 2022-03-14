# Baremetal image class
#
# This class is meant to be inherited by recipes for baremetal/RTOS applications
# It contains code that would be used by all of them, every recipe just needs to
# override certain variables.
#
# For scalability purposes, code within this class focuses on the "image" wiring
# to satisfy the OpenEmbedded image creation and testing infrastructure.
#
# See meta-skeleton for a working example.


# Toolchain should be baremetal or newlib based.
# TCLIBC="baremetal" or TCLIBC="newlib"
COMPATIBLE_HOST:libc-musl:class-target = "null"
COMPATIBLE_HOST:libc-glibc:class-target = "null"


inherit rootfs-postcommands

# Set some defaults, but these should be overriden by each recipe if required
IMGDEPLOYDIR ?= "${WORKDIR}/deploy-${PN}-image-complete"
BAREMETAL_BINNAME ?= "hello_baremetal_${MACHINE}"
IMAGE_LINK_NAME ?= "baremetal-helloworld-image-${MACHINE}"
IMAGE_NAME_SUFFIX ?= ""

do_rootfs[dirs] = "${IMGDEPLOYDIR} ${DEPLOY_DIR_IMAGE}"

do_image(){
    install ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.bin ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.bin
    install ${D}/${base_libdir}/firmware/${BAREMETAL_BINNAME}.elf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.elf
}

do_image_complete(){
    :
}

python do_rootfs(){
    from oe.utils import execute_pre_post_process
    from pathlib import Path

    # Write empty manifest file to satisfy test infrastructure
    deploy_dir = d.getVar('IMGDEPLOYDIR')
    link_name = d.getVar('IMAGE_LINK_NAME')
    manifest_name = d.getVar('IMAGE_MANIFEST')

    Path(manifest_name).touch()
    if os.path.exists(manifest_name) and link_name:
        manifest_link = deploy_dir + "/" + link_name + ".manifest"
        if os.path.lexists(manifest_link):
            os.remove(manifest_link)
        os.symlink(os.path.basename(manifest_name), manifest_link)
    # A lot of postprocess commands assume the existence of rootfs/etc
    sysconfdir = d.getVar("IMAGE_ROOTFS") + d.getVar('sysconfdir')
    bb.utils.mkdirhier(sysconfdir)

    execute_pre_post_process(d, d.getVar('ROOTFS_POSTPROCESS_COMMAND'))
}


# Assure binaries, manifest and qemubootconf are populated on DEPLOY_DIR_IMAGE
do_image_complete[dirs] = "${TOPDIR}"
SSTATETASKS += "do_image_complete"
SSTATE_SKIP_CREATION:task-image-complete = '1'
do_image_complete[sstate-inputdirs] = "${IMGDEPLOYDIR}"
do_image_complete[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_image_complete[stamp-extra-info] = "${MACHINE_ARCH}"
addtask do_image_complete after do_image before do_build

python do_image_complete_setscene () {
    sstate_setscene(d)
}
addtask do_image_complete_setscene

# QEMU generic Baremetal/RTOS parameters
QB_DEFAULT_KERNEL ?= "${IMAGE_LINK_NAME}.bin"
QB_MEM ?= "-m 256"
QB_DEFAULT_FSTYPE ?= "bin"
QB_DTB ?= ""
QB_OPT_APPEND:append = " -nographic"

# RISC-V tunes set the BIOS, unset, and instruct QEMU to
# ignore the BIOS and boot from -kernel
QB_DEFAULT_BIOS:qemuriscv64 = ""
QB_DEFAULT_BIOS:qemuriscv32 = ""
QB_OPT_APPEND:append:qemuriscv64 = " -bios none"
QB_OPT_APPEND:append:qemuriscv32 = " -bios none"


# Use the medium-any code model for the RISC-V 64 bit implementation,
# since medlow can only access addresses below 0x80000000 and RAM
# starts at 0x80000000 on RISC-V 64
# Keep RISC-V 32 using -mcmodel=medlow (symbols lie between -2GB:2GB)
CFLAGS:append:qemuriscv64 = " -mcmodel=medany"


# This next part is necessary to trick the build system into thinking
# its building an image recipe so it generates the qemuboot.conf
addtask do_rootfs before do_image after do_install
addtask do_image after do_rootfs before do_image_complete
addtask do_image_complete after do_image before do_build
inherit qemuboot

# Based on image.bbclass to make sure we build qemu
python(){
    # do_addto_recipe_sysroot doesnt exist for all recipes, but we need it to have
    # /usr/bin on recipe-sysroot (qemu) populated
    # The do_addto_recipe_sysroot dependency is coming from EXTRA_IMAGDEPENDS now,
    # we just need to add the logic to add its dependency to do_image.
    def extraimage_getdepends(task):
        deps = ""
        for dep in (d.getVar('EXTRA_IMAGEDEPENDS') or "").split():
        # Make sure we only add it for qemu
            if 'qemu' in dep:
                if ":" in dep:
                    deps += " %s " % (dep)
                else:
                    deps += " %s:%s" % (dep, task)
        return deps
    d.appendVarFlag('do_image', 'depends', extraimage_getdepends('do_populate_sysroot')) 
}
