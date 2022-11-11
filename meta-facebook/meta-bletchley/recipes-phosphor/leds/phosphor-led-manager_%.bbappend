FAN_INSTANCES="0 1 2 3"
FAN_INSTFMT="obmc-led-group-start@.service:obmc-led-group-start@fan{0}_good.service"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'FAN_INSTFMT', 'FAN_INSTANCES')}"

HOST_START_INSTFMT="obmc-led-group-start@.service:obmc-led-group-start@sled{0}_good.service"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_START_INSTFMT', 'OBMC_HOST_INSTANCES')}"

HOST_STOP_INSTFMT="obmc-led-group-stop@.service:obmc-led-group-stop@sled{0}_good.service"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_STOP_INSTFMT', 'OBMC_HOST_INSTANCES')}"
