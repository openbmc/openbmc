KBRANCH:genericx86  = "v5.4/standard/base"
KBRANCH:genericx86-64  = "v5.4/standard/base"
KBRANCH:edgerouter = "v5.4/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.4/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "31db2b47ac7d8508080fbb7344399b501216de66"
SRCREV_machine:genericx86-64 ?= "31db2b47ac7d8508080fbb7344399b501216de66"
SRCREV_machine:edgerouter ?= "706efec4c1e270ec5dda92275898cd465dfdc7dd"
SRCREV_machine:beaglebone-yocto ?= "706efec4c1e270ec5dda92275898cd465dfdc7dd"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.4.94"
LINUX_VERSION:genericx86-64 = "5.4.94"
LINUX_VERSION:edgerouter = "5.4.58"
LINUX_VERSION:beaglebone-yocto = "5.4.58"
