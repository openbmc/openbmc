KBRANCH_genericx86  = "v5.10/standard/base"
KBRANCH_genericx86-64  = "v5.10/standard/base"
KBRANCH_edgerouter = "v5.10/standard/edgerouter"
KBRANCH_beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KMACHINE_beaglebone-yocto ?= "beaglebone"

SRCREV_machine_genericx86 ?= "ab49d2db98bdee2c8c6e17fb59ded9e5292b0f41"
SRCREV_machine_genericx86-64 ?= "ab49d2db98bdee2c8c6e17fb59ded9e5292b0f41"
SRCREV_machine_edgerouter ?= "274d63799465eebfd201b3e8251f16d29e93a978"
SRCREV_machine_beaglebone-yocto ?= "ab49d2db98bdee2c8c6e17fb59ded9e5292b0f41"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION_genericx86 = "5.10.43"
LINUX_VERSION_genericx86-64 = "5.10.43"
LINUX_VERSION_edgerouter = "5.10.43"
LINUX_VERSION_beaglebone-yocto = "5.10.43"
