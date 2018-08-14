PREFERRED_PROVIDER_virtual/kernel ?= "linux-nuvoton"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot ?= "u-boot-nuvoton"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-fw-utils-nuvoton"

MACHINEOVERRIDES .= ":nuvoton"
