# Copyright (C) 2016 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# systemd-boot.bbclass - The "systemd-boot" is essentially the gummiboot merged into systemd.
#                        The original standalone gummiboot project is dead without any more
#                        maintenance.
#
# Set EFI_PROVIDER = "systemd-boot" to use systemd-boot on your live images instead of grub-efi
# (images built by image-live.bbclass)

do_bootimg[depends] += "${MLPREFIX}systemd-boot:do_deploy"

require conf/image-uefi.conf
# Need UUID utility code.
inherit fs-uuid

efi_populate() {
        efi_populate_common "$1" systemd

        # systemd-boot requires these paths for configuration files
        # they are not customizable so no point in new vars
        install -d ${DEST}/loader
        install -d ${DEST}/loader/entries
        install -m 0644 ${SYSTEMD_BOOT_CFG} ${DEST}/loader/loader.conf
        for i in ${SYSTEMD_BOOT_ENTRIES}; do
            install -m 0644 ${i} ${DEST}/loader/entries
        done
}

efi_iso_populate:append() {
        cp -r $iso_dir/loader ${EFIIMGDIR}
}

inherit systemd-boot-cfg
