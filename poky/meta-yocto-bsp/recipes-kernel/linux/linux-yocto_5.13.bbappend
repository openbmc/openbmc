KBRANCH:genericx86  = "v5.13/standard/base"
KBRANCH:genericx86-64  = "v5.13/standard/base"
KBRANCH:edgerouter = "v5.13/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.13/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "c1eb1eaf6fd3f1302b89194f629eafb9368a326a"
SRCREV_machine:genericx86-64 ?= "c1eb1eaf6fd3f1302b89194f629eafb9368a326a"
SRCREV_machine:edgerouter ?= "2d40c76f86e94252bbfbff4294b43b33de780cd5"
SRCREV_machine:beaglebone-yocto ?= "18666b33d7ceaf095da5d58fecd6fcb070932434"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.13.7"
LINUX_VERSION:genericx86-64 = "5.13.7"
LINUX_VERSION:edgerouter = "5.13.7"
LINUX_VERSION:beaglebone-yocto = "5.13.7"
