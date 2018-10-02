
ZynqMP - PMU ROM
----------------

Since Xilinx tool release v2017.1 multiple components (arm-trusted-firmware,
linux, u-boot, etc.) require the PMU firmware to be loaded. For QEMU this also
means that the PMU ROM must be loaded so that the PMU firmware can be used.

The PMU ROM is not available for download separately from a location that can be
accessed without a Xilinx account. As such the PMU ROM must be obtained manually
by the user. The PMU ROM is available in the ZCU102 PetaLinux BSP, but can be
extracted without the need for the PetaLinux tools.

Download the BSP (you will need a Xilinx account and agreement to terms):

https://www.xilinx.com/member/forms/download/xef.html?filename=xilinx-zcu102-v2017.1-final.bsp&akdm=1

Once downloaded the PMU ROM can be extracted using the following command and
place `pmu-rom.elf` in the `deploy/images/zcu102-zynqmp/` directory.

```
# tar -O -xf xilinx-zcu102-v2017.1-final.bsp \
    xilinx-zcu102-2017.1/pre-built/linux/images/pmu_rom_qemu_sha3.elf > pmu-rom.elf
```

