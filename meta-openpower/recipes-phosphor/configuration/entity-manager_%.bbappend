FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Systems with IBM processors tend to use IBM-formatted VPD, so fru-device
# shouldn't be enabled.
SYSTEMD_AUTO_ENABLE:fru-device:ibm-power-cpu = "disable"
