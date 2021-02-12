KBRANCH_genericx86  = "v5.10/standard/base"
KBRANCH_genericx86-64  = "v5.10/standard/base"
KBRANCH_edgerouter = "v5.10/standard/edgerouter"
KBRANCH_beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KMACHINE_beaglebone-yocto ?= "beaglebone"

SRCREV_machine_genericx86 ?= "cdca78778415b4b3bd64e8390ee8adf04bf7e17a"
SRCREV_machine_genericx86-64 ?= "cdca78778415b4b3bd64e8390ee8adf04bf7e17a"
SRCREV_machine_edgerouter ?= "2e1fb8f84f09ca768eb531f33a126a40bb90e791"
SRCREV_machine_beaglebone-yocto ?= "cdca78778415b4b3bd64e8390ee8adf04bf7e17a"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION_genericx86 = "5.10.12"
LINUX_VERSION_genericx86-64 = "5.10.12"
LINUX_VERSION_edgerouter = "5.10.12"
LINUX_VERSION_beaglebone-yocto = "5.10.12"
