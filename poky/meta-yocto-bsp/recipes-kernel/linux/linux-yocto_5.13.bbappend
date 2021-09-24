KBRANCH:genericx86  = "v5.13/standard/base"
KBRANCH:genericx86-64  = "v5.13/standard/base"
KBRANCH:edgerouter = "v5.13/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.13/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "7280c93f5599946db3add473eeb05b34c364938d"
SRCREV_machine:genericx86-64 ?= "7280c93f5599946db3add473eeb05b34c364938d"
SRCREV_machine:edgerouter ?= "a832a0390e96c4f014d7b2bf9f161ac9477140f7"
SRCREV_machine:beaglebone-yocto ?= "dbdc921374c057a75b2df92302124994e241ca51"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.13.15"
LINUX_VERSION:genericx86-64 = "5.13.15"
LINUX_VERSION:edgerouter = "5.13.15"
LINUX_VERSION:beaglebone-yocto = "5.13.15"
