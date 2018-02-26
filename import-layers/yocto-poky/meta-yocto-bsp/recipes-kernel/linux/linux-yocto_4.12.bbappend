KBRANCH_genericx86  = "standard/base"
KBRANCH_genericx86-64  = "standard/base"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KBRANCH_edgerouter = "standard/edgerouter"
KBRANCH_beaglebone = "standard/beaglebone"
KBRANCH_mpc8315e-rdb = "standard/fsl-mpc8315e-rdb"

SRCREV_machine_genericx86    ?= "1c4ad569af3e23a77994235435040e322908687f"
SRCREV_machine_genericx86-64 ?= "1c4ad569af3e23a77994235435040e322908687f"
SRCREV_machine_edgerouter ?= "257f843ea367744620f1d92910afd2f454e31483"
SRCREV_machine_beaglebone-yocto ?= "257f843ea367744620f1d92910afd2f454e31483"
SRCREV_machine_mpc8315e-rdb ?= "014560874f9eb2a86138c9cc35046ff1720485e1"


COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone = "beaglebone"
COMPATIBLE_MACHINE_mpc8315e-rdb = "mpc8315e-rdb"

LINUX_VERSION_genericx86 = "4.12.20"
LINUX_VERSION_genericx86-64 = "4.12.20"
LINUX_VERSION_edgerouter = "4.12.19"
LINUX_VERSION_beaglebone-yocto = "4.12.19"
LINUX_VERSION_mpc8315e-rdb = "4.12.19"
