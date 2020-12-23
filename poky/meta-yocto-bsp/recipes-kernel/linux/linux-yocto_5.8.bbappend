KBRANCH_genericx86  = "v5.8/standard/base"
KBRANCH_genericx86-64  = "v5.8/standard/base"
KBRANCH_edgerouter = "v5.8/standard/edgerouter"
KBRANCH_beaglebone-yocto = "v5.8/standard/beaglebone"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KMACHINE_beaglebone-yocto ?= "beaglebone"

SRCREV_machine_genericx86 ?= "5981001bf03f0d0c1cf641a2287ab6368baf628a"
SRCREV_machine_genericx86-64 ?= "5981001bf03f0d0c1cf641a2287ab6368baf628a"
SRCREV_machine_edgerouter ?= "912adf166eb0688e011154048f5fa0e5863249c3"
SRCREV_machine_beaglebone-yocto ?= "912adf166eb0688e011154048f5fa0e5863249c3"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION_genericx86 = "5.8.13"
LINUX_VERSION_genericx86-64 = "5.8.13"
LINUX_VERSION_edgerouter = "5.8.2"
LINUX_VERSION_beaglebone-yocto = "5.8.2"
