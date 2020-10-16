KBRANCH_genericx86  = "v5.4/standard/base"
KBRANCH_genericx86-64  = "v5.4/standard/base"
KBRANCH_edgerouter = "v5.4/standard/edgerouter"
KBRANCH_beaglebone-yocto = "v5.4/standard/beaglebone"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"
KMACHINE_beaglebone-yocto ?= "beaglebone"

SRCREV_machine_genericx86 ?= "cfcdd63145c0d741e57ee3e3e58f794229c6c09c"
SRCREV_machine_genericx86-64 ?= "cfcdd63145c0d741e57ee3e3e58f794229c6c09c"
SRCREV_machine_edgerouter ?= "706efec4c1e270ec5dda92275898cd465dfdc7dd"
SRCREV_machine_beaglebone-yocto ?= "706efec4c1e270ec5dda92275898cd465dfdc7dd"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION_genericx86 = "5.4.69"
LINUX_VERSION_genericx86-64 = "5.4.69"
LINUX_VERSION_edgerouter = "5.4.58"
LINUX_VERSION_beaglebone-yocto = "5.4.58"
