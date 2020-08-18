include conf/machine/include/ibm-power9-cpu.inc
OBMC_MACHINE_FEATURES += 'op-fsi p9-cfam-override'
PREFERRED_PROVIDER_virtual/obmc-host-ctl = "p9-host-start"
