require u-boot-common.inc
require u-boot.inc

DEPENDS += "bc-native dtc-native gnutls-native python3-pyelftools-native"

SRC_URI += "file://CVE-2026-46728.patch"

CVE_STATUS[CVE-2026-33243] = "cpe-incorrect: NVD currently maps this CVE to denx:u-boot, but that mapping is incorrect for U-Boot; the U-Boot-side FIT issue is tracked separately as CVE-2026-46728 and is fixed by the included U-Boot backport."

# workarounds for aarch64 kvm qemu boot regressions
SRC_URI:append:qemuarm64 = " file://disable-CONFIG_BLOBLIST.cfg"
SRC_URI:append:genericarm64 = " file://disable-CONFIG_BLOBLIST.cfg"

SRC_URI_RISCV = "\
    file://u-boot-riscv-isa_clear.cfg \
    ${@bb.utils.contains    ("TUNE_FEATURES", "a",      "file://u-boot-riscv-isa_a.cfg", "", d)} \
    ${@bb.utils.contains    ("TUNE_FEATURES", "f",      "file://u-boot-riscv-isa_f.cfg", "", d)} \
    ${@bb.utils.contains    ("TUNE_FEATURES", "d",      "file://u-boot-riscv-isa_d.cfg", "", d)} \
    ${@bb.utils.contains    ("TUNE_FEATURES", "c",      "file://u-boot-riscv-isa_c.cfg", "", d)} \
    ${@bb.utils.contains_any("TUNE_FEATURES", "b zbb",  "file://u-boot-riscv-isa_zbb.cfg", "", d)} \
    ${@bb.utils.contains    ("TUNE_FEATURES", "zicbom", "file://u-boot-riscv-isa_zicbom.cfg", "", d)} \
    "

SRC_URI:append:riscv32 = "${SRC_URI_RISCV}"
SRC_URI:append:riscv64 = "${SRC_URI_RISCV}"
