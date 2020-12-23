PACKAGECONFIG_append = " ubifs_layout"

SYSTEMD_SERVICE_${PN} += " \
        obmc-flash-bios-ubiattach.service \
        obmc-flash-bios-ubimount@.service \
        obmc-flash-bios-ubiumount-ro@.service \
        obmc-flash-bios-ubiumount-rw@.service \
        obmc-flash-bios-ubipatch.service \
        obmc-flash-bios-ubiremount.service \
        obmc-flash-bios-cleanup.service \
        "
