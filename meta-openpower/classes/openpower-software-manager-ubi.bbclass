PACKAGECONFIG_append = " ubifs_layout"

SYSTEMD_SERVICE_${PN} += " \
        obmc-flash-bios-ubiattach.service \
        obmc-flash-bios-ubimount@.service \
        obmc-flash-bios-ubiumount-ro@.service \
        obmc-flash-bios-ubiumount-rw@.service \
        obmc-flash-bios-ubipatch.service \
        obmc-flash-bios-ubiremount.service \
        obmc-flash-bios-cleanup.service \
        obmc-flash-bios-enable-clearvolatile@.service \
        obmc-flash-bios-check-clearvolatile@.service \
        "

ENABLE_CLEAR_VOLATILE_TMPL = "obmc-flash-bios-enable-clearvolatile@.service"
HOST_START_TGTFMT = "obmc-host-start@{0}.target"
ENABLE_CLEAR_VOLATILE_INSTFMT = "obmc-flash-bios-enable-clearvolatile@{0}.service"
ENABLE_CLEAR_VOLATILE_START_FMT = "../${ENABLE_CLEAR_VOLATILE_TMPL}:${HOST_START_TGTFMT}.requires/${ENABLE_CLEAR_VOLATILE_INSTFMT}"

CHECK_CLEAR_VOLATILE_TMPL = "obmc-flash-bios-check-clearvolatile@.service"
HOST_STARTMIN_TGTFMT = "obmc-host-startmin@{0}.target"
CHECK_CLEAR_VOLATILE_INSTFMT = "obmc-flash-bios-check-clearvolatile@{0}.service"
CHECK_CLEAR_VOLATILE_START_FMT = "../${CHECK_CLEAR_VOLATILE_TMPL}:${HOST_STARTMIN_TGTFMT}.requires/${CHECK_CLEAR_VOLATILE_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ENABLE_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHECK_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"
