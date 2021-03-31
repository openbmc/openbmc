KBRANCH_genericx86  = "v5.10/standard/base"
KBRANCH_genericx86-64  = "v5.10/standard/base"
KBRANCH_edgerouter = "v5.10/standard/edgerouter"
KBRANCH_beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KMACHINE_beaglebone-yocto ?= "beaglebone"

SRCREV_machine_genericx86 ?= "8c516ced69f41563404ada0bea315a55bcf1df6f"
SRCREV_machine_genericx86-64 ?= "8c516ced69f41563404ada0bea315a55bcf1df6f"
SRCREV_machine_edgerouter ?= "965ab3ab746ae8a1158617b6302d9c218ffbbb66"
SRCREV_machine_beaglebone-yocto ?= "8c516ced69f41563404ada0bea315a55bcf1df6f"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION_genericx86 = "5.10.21"
LINUX_VERSION_genericx86-64 = "5.10.21"
LINUX_VERSION_edgerouter = "5.10.21"
LINUX_VERSION_beaglebone-yocto = "5.10.21"
