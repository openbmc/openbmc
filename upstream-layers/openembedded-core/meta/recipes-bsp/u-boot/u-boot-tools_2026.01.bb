require u-boot-common.inc
require u-boot-tools.inc

SRC_URI += "file://CVE-2026-46728.patch"

CVE_STATUS[CVE-2026-33243] = "cpe-incorrect: NVD currently maps this CVE to denx:u-boot, but that mapping is incorrect for U-Boot; the U-Boot-side FIT issue is tracked separately as CVE-2026-46728 and is fixed by the included U-Boot backport."

CVE_STATUS[CVE-2026-29007] = "not-applicable-config: tools-only_defconfig disables networking; net/tcp.c is not compiled into u-boot-tools."
CVE_STATUS[CVE-2026-29008] = "not-applicable-config: tools-only_defconfig disables networking; net/tcp.c is not compiled into u-boot-tools."
CVE_STATUS[CVE-2026-29009] = "not-applicable-config: tools-only_defconfig disables networking; net/nfs.c is not compiled into u-boot-tools."
