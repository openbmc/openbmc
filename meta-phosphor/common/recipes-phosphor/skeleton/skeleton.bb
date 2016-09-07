SUMMARY = "Temp placeholder for skeleton function"
DESCRIPTION = "Temp placeholder for skeleton repository"
HOMEPAGE = "http://github.com/openbmc/skeleton"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

inherit obmc-phosphor-chassis-mgmt
inherit obmc-phosphor-fan-mgmt
inherit obmc-phosphor-flash-mgmt
inherit obmc-phosphor-sensor-mgmt
inherit obmc-phosphor-system-mgmt

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
