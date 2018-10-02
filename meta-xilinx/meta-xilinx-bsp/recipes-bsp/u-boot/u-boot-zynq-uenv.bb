SUMMARY = "U-Boot uEnv.txt SD boot environment generation for Zynq targets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynq = ".*"
COMPATIBLE_MACHINE_zynqmp = ".*"

inherit deploy

def bootfiles_bitstream(d):
    expectedfiles = [("bitstream", True)]
    expectedexts = [(".bit", True), (".bin", False)]
    # search for bitstream paths, use the renamed file. First matching is used
    for f in (d.getVar("IMAGE_BOOT_FILES") or "").split():
        sf, rf = f, f
        if ';' in f:
            sf, rf = f.split(';')

        # skip boot.bin and u-boot.bin, it is not a bitstream
        skip = ["boot.bin", "u-boot.bin"]
        if sf in skip or rf in skip:
            continue

        for e, t in expectedfiles:
            if sf == e or rf == e:
                return rf, t
        for e, t in expectedexts:
            if sf.endswith(e) or rf.endswith(e):
                return rf, t
    return "", False

def bootfiles_dtb_filepath(d):
    if d.getVar("IMAGE_BOOT_FILES"):
        dtbs = d.getVar("IMAGE_BOOT_FILES").split(" ")
        # IMAGE_BOOT_FILES has extra renaming info in the format '<source>;<target>'
        dtbs = [f.split(";")[0] for f in dtbs]
        dtbs = [f for f in dtbs if f.endswith(".dtb")]
        if len(dtbs) != 0:
            return dtbs[0]
    return ""

def uboot_boot_cmd(d):
    if d.getVar("KERNEL_IMAGETYPE") in ["uImage", "fitImage"]:
        return "bootm"
    if d.getVar("KERNEL_IMAGETYPE") in ["zImage"]:
        return "bootz"
    if d.getVar("KERNEL_IMAGETYPE") in ["Image"]:
        return "booti"
    raise bb.parse.SkipRecipe("Unsupport kernel image type")

def uenv_populate(d):
    # populate the environment values
    env = {}

    env["machine_name"] = d.getVar("MACHINE")

    env["kernel_image"] = d.getVar("KERNEL_IMAGETYPE")
    env["kernel_load_address"] = d.getVar("KERNEL_LOAD_ADDRESS")

    env["devicetree_image"] = bootfiles_dtb_filepath(d)
    env["devicetree_load_address"] = d.getVar("DEVICETREE_LOAD_ADDRESS")

    env["bootargs"] = d.getVar("KERNEL_BOOTARGS")

    env["loadkernel"] = "fatload mmc 0 ${kernel_load_address} ${kernel_image}"
    env["loaddtb"] = "fatload mmc 0 ${devicetree_load_address} ${devicetree_image}"
    env["bootkernel"] = "run loadkernel && run loaddtb && " + uboot_boot_cmd(d) + " ${kernel_load_address} - ${devicetree_load_address}"

    # default uenvcmd does not load bitstream
    env["uenvcmd"] = "run bootkernel"

    bitstream, bitstreamtype = bootfiles_bitstream(d)
    if bitstream:
        env["bitstream_image"] = bitstream
        env["bitstream_load_address"] = "0x100000"

        # if bitstream is "bit" format use loadb, otherwise use load
        env["bitstream_type"] = "loadb" if bitstreamtype else "load"

        # load bitstream first with loadfpa
        env["loadfpga"] = "fatload mmc 0 ${bitstream_load_address} ${bitstream_image} && fpga ${bitstream_type} 0 ${bitstream_load_address} ${filesize}"
        env["uenvcmd"] = "run loadfpga && run bootkernel"

    return env

# bootargs, default to booting with the rootfs device being partition 2 of the first mmc device
KERNEL_BOOTARGS_zynq = "earlyprintk console=ttyPS0,115200 root=/dev/mmcblk0p2 rw rootwait"
KERNEL_BOOTARGS_zynqmp = "earlycon clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait"

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

