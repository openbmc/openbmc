SUMMARY = "Temp placeholder for skeleton function"
DESCRIPTION = "Temp placeholder for skeleton repository"
HOMEPAGE = "http://github.com/openbmc/skeleton"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

PROVIDES += "virtual/obmc-chassis-mgmt"
RPROVIDES_${PN} += "virtual-obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
RPROVIDES_${PN} += "virtual-obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
RPROVIDES_${PN} += "virtual-obmc-flash-mgmt"
PROVIDES += "virtual/obmc-sensor-mgmt"
RPROVIDES_${PN} += "virtual-obmc-sensor-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"
RPROVIDES_${PN} += "virtual-obmc-system-mgmt"

RDEPENDS_${PN} += " \
        obmc-button-power \
        obmc-button-reset \
        obmc-control-chassis \
        obmc-hostcheckstop \
        obmc-mgr-inventory \
        obmc-op-control-power \
        obmc-pcie-detect \
        obmc-watchdog \
        obmc-pydevtools \
        obmc-control-fan \
        obmc-flash-bios \
        obmc-flash-bmc \
        obmc-mgr-download \
        obmc-op-flasher \
        obmc-op-control-host \
        obmc-control-led \
        obmc-hwmon \
        obmc-mgr-sensor \
        obmc-control-bmc \
        obmc-mgr-state \
        obmc-mgr-system \
        "
