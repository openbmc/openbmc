# This include is used to setup default QEMU and qemuboot config for meta-xilinx
# machines.

# Use the xilinx specific version for these users
IMAGE_CLASSES += "qemuboot-xilinx"

# depend on qemu-helper-native, which will depend on QEMU
EXTRA_IMAGEDEPENDS += "qemu-helper-native"

PREFERRED_PROVIDER_qemu-helper-native = "qemu-xilinx-helper-native"
PREFERRED_PROVIDER_qemu = "qemu-xilinx"
PREFERRED_PROVIDER_qemu-native = "qemu-xilinx-native"
PREFERRED_PROVIDER_nativesdk-qemu = "nativesdk-qemu-xilinx"

def qemu_default_dtb(d):
    if d.getVar("IMAGE_BOOT_FILES", True):
        dtbs = d.getVar("IMAGE_BOOT_FILES", True).split(" ")
        # IMAGE_BOOT_FILES has extra renaming info in the format '<source>;<target>'
        # Note: Wildcard sources work here only because runqemu expands them at run time
        dtbs = [f.split(";")[0] for f in dtbs]
        dtbs = [f for f in dtbs if f.endswith(".dtb")]
        if len(dtbs) != 0:
            return dtbs[0]
    return ""

def qemu_default_serial(d):
    if d.getVar("SERIAL_CONSOLES", True):
        first_console = d.getVar("SERIAL_CONSOLES", True).split(" ")[0]
        speed, console = first_console.split(";", 1)
        # zynqmp uses earlycon and stdout (in dtb)
        if "zynqmp" in d.getVar("MACHINEOVERRIDES", True).split(":"):
            return ""
        return "console=%s,%s earlyprintk" % (console, speed)
    return ""

def qemu_target_binary(d):
    ta = d.getVar("TARGET_ARCH", True)
    if ta == "microblazeeb":
        ta = "microblaze"
    elif ta == "arm":
        ta = "aarch64"
    return "qemu-system-%s" % ta

def qemu_zynqmp_unhalt(d, multiarch):
    if multiarch:
        return "-global xlnx,zynqmp-boot.cpu-num=0 -global xlnx,zynqmp-boot.use-pmufw=true"
    return "-device loader,addr=0xfd1a0104,data=0x8000000e,data-len=4 -device loader,addr=0xfd1a0104,data=0x8000000e,data-len=4"

# For qemuboot, default setup across all machines in meta-xilinx
QB_SYSTEM_NAME_aarch64 ?= "${@qemu_target_binary(d)}-multiarch"
QB_SYSTEM_NAME ?= "${@qemu_target_binary(d)}"
QB_DEFAULT_FSTYPE ?= "cpio"
QB_DTB ?= "${@qemu_default_dtb(d)}"
QB_KERNEL_CMDLINE_APPEND ?= "${@qemu_default_serial(d)}"

