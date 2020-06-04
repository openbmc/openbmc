# eMMC-specific configuration for the phosphor-manager-software package

PACKAGECONFIG_append = " mmc_layout"

EXTRA_OEMESON += "-Dactive-bmc-max-allowed=2"
EXTRA_OEMESON += "-Dmedia-dir='/media'"

