# eMMC-specific configuration for the phosphor-manager-software package

PACKAGECONFIG:append = " mmc_layout"

EXTRA_OEMESON:append = " \
    -Dactive-bmc-max-allowed=2 \
    -Dmedia-dir='/media' \
    -Doptional-images='image-hostfw' \
"

RDEPENDS:phosphor-software-manager-updater-mmc += " \
    gptfdisk \
    parted \
    zstd \
"

SYSTEMD_SERVICE:phosphor-software-manager-updater-mmc += " \
    obmc-flash-mmc@.service \
    obmc-flash-mmc-remove@.service \
    obmc-flash-mmc-setprimary@.service \
    obmc-flash-mmc-mount.service \
    obmc-flash-mmc-umount.service \
    obmc-flash-mmc-mirroruboot.service \
"
