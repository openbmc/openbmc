do_install:append:mf-fb-secondary-emmc() {
        # create eMMC mount point
        install -m 0755 -d ${D}/mnt/emmc

        # insert fstab entry for eMMC
        FSTAB_EMMC="/dev/mmcblk0 /mnt/emmc btrfs compress=zstd,discard,nofail,x-systemd.device-timeout=10s 0 0"
        echo "$FSTAB_EMMC" >> ${D}${sysconfdir}/fstab
}
