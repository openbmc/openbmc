OBMC_MACHINE_FEATURES += "\
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-sensor-mgmt \
        obmc-phosphor-flash-mgmt \
        "

VIRTUAL-RUNTIME_obmc-phosphor-fan-ctl = " \
       obmc-phosphor-qemu \
       "
VIRTUAL-RUNTIME_obmc-phosphor-sensor-ctl = " \
       obmc-phosphor-qemu \
       "
VIRTUAL-RUNTIME_obmc-phosphor-chassis-ctl = " \
       obmc-phosphor-qemu \
       "
VIRTUAL-RUNTIME_obmc-phosphor-flash-ctl = " \
       obmc-phosphor-qemu \
       "
