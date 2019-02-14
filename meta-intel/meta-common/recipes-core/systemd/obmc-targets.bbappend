# Remove these files since they are provided by obmc-intel-targets
SYSTEMD_SERVICE_${PN}_remove += " obmc-host-start@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-host-stop@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-host-reboot@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-host-startmin@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-chassis-poweron@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-chassis-poweroff@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-chassis-hard-poweroff@.target"
SYSTEMD_SERVICE_${PN}_remove += " obmc-chassis-powerreset@.target"
