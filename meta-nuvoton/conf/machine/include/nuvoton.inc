PREFERRED_PROVIDER_virtual/kernel ?= "linux-nuvoton"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-nuvoton"

PREFERRED_VERSION_nbd ?= "3.20"

MACHINEOVERRIDES .= ":nuvoton"

# fix build mmc distro error
IMAGE_FSTYPES:remove:df-phosphor-mmc = "mtd-static mtd-static-tar mtd-static-alltar"
