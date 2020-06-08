EXTRA_OECMAKE_append = " \
    -DBMCWEB_INSECURE_ENABLE_REDFISH_FW_TFTP_UPDATE=ON \
"

inherit obmc-phosphor-discovery-service

REGISTERED_SERVICES_${PN} += "obmc_redfish:tcp:443:"
REGISTERED_SERVICES_${PN} += "obmc_rest:tcp:443:"
