SYSTEMD_SERVICE_${PN}_append_ibm-ac-server = " first-boot-set-mac@eth0.service"
SYSTEMD_SERVICE_${PN}_append_mihawk = " first-boot-set-mac@eth0.service first-boot-set-mac@eth1.service"