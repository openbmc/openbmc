PACKAGECONFIG:append:ibm-enterprise = " phal"

# On huygens the SPI mux is handled differently; suppress the multi-user.target install
SYSTEMD_SERVICE:${PN}:remove:huygens = "set-spi-mux.service"
