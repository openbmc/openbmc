FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append:ibm-enterprise = " phal"

# On huygens the SPI mux is handled differently; suppress the multi-user.target install
SYSTEMD_SERVICE:${PN}:remove:huygens = "set-spi-mux.service"

# On non-huygens machines, ensure set-spi-mux.service runs after
# phosphor-reset-chassis-running@0.service to properly support reset/reloads
# where chassis power may already be on. Huygens has a dedicated code path
# that calls set-spi-mux.service directly when appropriate, so this
# multi-user.target dependency is only needed on all other machines.
SYSTEMD_OVERRIDE:${PN}:append:ibm-enterprise = " set-spi-mux-chassis-running.conf:set-spi-mux.service.d/set-spi-mux-chassis-running.conf"
SYSTEMD_OVERRIDE:${PN}:remove:huygens = "set-spi-mux-chassis-running.conf:set-spi-mux.service.d/set-spi-mux-chassis-running.conf"
