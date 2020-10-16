SUMMARY = "U-Boot uEnv.txt SD boot environment generation for Zynq targets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

python () {
    # The device trees must be populated in the deploy directory to correctly
    # detect them and their names. This means that this recipe needs to depend
    # on those deployables just like the image recipe does.
    deploydeps = ["virtual/kernel"]
    for i in (d.getVar("MACHINE_ESSENTIAL_EXTRA_RDEPENDS") or "").split():
        if i != d.getVar("BPN"):
            deploydeps.append(i)
    for i in (d.getVar("EXTRA_IMAGEDEPENDS") or "").split():
        if i != d.getVar("BPN"):
            deploydeps.append(i)

    # add as DEPENDS since the targets might not have do_deploy tasks
    if len(deploydeps) != 0:
        d.appendVar("DEPENDS", " " + " ".join(deploydeps))
}

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynq = ".*"
COMPATIBLE_MACHINE_zynqmp = ".*"

inherit deploy image-wic-utils

def uboot_boot_cmd(d):
    if d.getVar("KERNEL_IMAGETYPE") in ["uImage", "fitImage"]:
        return "bootm"
    if d.getVar("KERNEL_IMAGETYPE") in ["zImage"]:
        return "bootz"
    if d.getVar("KERNEL_IMAGETYPE") in ["Image"]:
        return "booti"
    raise bb.parse.SkipRecipe("Unsupport kernel image type")

def get_sdbootdev(d):
    if d.getVar("SOC_FAMILY") in ["zynqmp"]:
        return "${sdbootdev}"
    else:
        return "0"

def uenv_populate(d):
    # populate the environment values
    env = {}

    env["machine_name"] = d.getVar("MACHINE")

    env["kernel_image"] = d.getVar("KERNEL_IMAGETYPE")
    env["kernel_load_address"] = d.getVar("KERNEL_LOAD_ADDRESS")

    env["devicetree_image"] = boot_files_dtb_filepath(d)
    env["devicetree_load_address"] = d.getVar("DEVICETREE_LOAD_ADDRESS")

    env["bootargs"] = d.getVar("KERNEL_BOOTARGS")

    env["loadkernel"] = "fatload mmc " + get_sdbootdev(d) + " ${kernel_load_address} ${kernel_image}"
    env["loaddtb"] = "fatload mmc  " + get_sdbootdev(d) + " ${devicetree_load_address} ${devicetree_image}"
    env["bootkernel"] = "run loadkernel && run loaddtb && " + uboot_boot_cmd(d) + " ${kernel_load_address} - ${devicetree_load_address}"

    if d.getVar("SOC_FAMILY") in ["zynqmp"]:
        env["bootkernel"] = "setenv bootargs " +  d.getVar("KERNEL_BOOTARGS") + " ; " + env["bootkernel"]

    # default uenvcmd does not load bitstream
    env["uenvcmd"] = "run bootkernel"

    bitstream, bitstreamtype = boot_files_bitstream(d)
    if bitstream:
        env["bitstream_image"] = bitstream
        env["bitstream_load_address"] = "0x100000"

        # if bitstream is "bit" format use loadb, otherwise use load
        env["bitstream_type"] = "loadb" if bitstreamtype else "load"

        # load bitstream first with loadfpa
        env["loadfpga"] = "fatload mmc " + get_sdbootdev(d) + " ${bitstream_load_address} ${bitstream_image} && fpga ${bitstream_type} 0 ${bitstream_load_address} ${filesize}"
        env["uenvcmd"] = "run loadfpga && run bootkernel"

    return env

# bootargs, default to booting with the rootfs device being partition 2
KERNEL_BOOTARGS_zynq = "earlyprintk console=ttyPS0,115200 root=/dev/mmcblk0p2 rw rootwait"
KERNEL_BOOTARGS_zynqmp = "earlycon clk_ignore_unused root=/dev/mmcblk${sdbootdev}p2 rw rootwait"

KERNEL_LOAD_ADDRESS_zynq = "0x2080000"
KERNEL_LOAD_ADDRESS_zynqmp = "0x80000"
DEVICETREE_LOAD_ADDRESS_zynq = "0x2000000"
DEVICETREE_LOAD_ADDRESS_zynqmp = "0x4000000"

python do_compile() {
    env = uenv_populate(d)
    with open(d.expand("${WORKDIR}/uEnv.txt"), "w") as f:
        for k, v in env.items():
            f.write("{0}={1}\n".format(k, v))
}

FILES_${PN} += "/boot/uEnv.txt"

do_install() {
	install -Dm 0644 ${WORKDIR}/uEnv.txt ${D}/boot/uEnv.txt
}

do_deploy() {
	install -Dm 0644 ${WORKDIR}/uEnv.txt ${DEPLOYDIR}/uEnv.txt
}
addtask do_deploy after do_compile before do_build

