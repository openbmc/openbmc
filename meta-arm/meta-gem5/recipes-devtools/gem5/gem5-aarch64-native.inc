# gem5 aarch64 specific configuration

# Build arm64 gem5
GEM5_BUILD_CONFIGS ?= "build/ARM/gem5.${GEM5_BUILD_VARIANT}"

SRC_URI += "file://start-gem5.sh"

inherit deploy

# Parameters for the start script

GEM5_RUN_CONFIG ?= "gem5.${GEM5_BUILD_VARIANT}"

# Linux kernel file to boot
GEM5_RUN_KERNEL ?= "vmlinux"

# Disk Image to use
GEM5_RUN_DISK ?= "*-${MACHINE}.ext4"

# DTB to use
GEM5_RUN_DTB ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE'))}"

# Linux command line to pass
GEM5_RUN_CMDLINE ?= "earlyprintk=pl011,0x1c090000 console=ttyAMA0 rw mem=512MB root=/dev/sda rootwait"

# Extra arguments to pass to gem5
GEM5_RUN_EXTRA ?= "--mem-size=512MB -n 4 --machine-type=VExpress_GEM5_V2"

#This is required so that our binaries are in the sysroot. We need this
# to have both gem5 required libraries and gem5 in the same sysroot.
addtask addto_recipe_sysroot after do_populate_sysroot before do_build

do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_TOOLS}"
do_deploy() {
    install -d ${DEPLOYDIR}

    install -m 755 ${WORKDIR}/start-gem5.sh ${DEPLOYDIR}/.
}
addtask deploy before do_build after do_compile

