# eMMC-specific configuration for the phosphor-manager-software package

PACKAGECONFIG_append = " mmc_layout"

EXTRA_OEMESON += "-Dactive-bmc-max-allowed=2"
EXTRA_OEMESON += "-Dmedia-dir='/media'"
EXTRA_OEMESON += "-Doptional-images='image-hostfw'"

RDEPENDS_phosphor-software-manager-updater-mmc += " \
    gptfdisk \
    parted \
    zstd \
"

SYSTEMD_SERVICE_phosphor-software-manager-updater-mmc += " \
    obmc-flash-mmc@.service \
    obmc-flash-mmc-remove@.service \
    obmc-flash-mmc-setprimary@.service \
    obmc-flash-mmc-mount.service \
    obmc-flash-mmc-umount.service \
"
