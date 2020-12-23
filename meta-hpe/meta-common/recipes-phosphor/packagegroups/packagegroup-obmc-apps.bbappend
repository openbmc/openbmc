RDEPENDS_${PN}-logging += "phosphor-logging"
RDEPENDS_${PN}-extras += " bmcweb \
                           phosphor-webui \
                           phosphor-image-signing \
                           phosphor-pid-control \
"

RDEPENDS_${PN}-fan-control = " \
         ${VIRTUAL-RUNTIME_obmc-fan-control} \
         "
