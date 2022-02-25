KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "7f685244afb3acd13e94968312580b63d7296705"
SRCREV_machine:genericx86-64 ?= "7f685244afb3acd13e94968312580b63d7296705"
SRCREV_machine:edgerouter ?= "5a8ec126c297dd9e410eb23685003f670f8f3d57"
SRCREV_machine:beaglebone-yocto ?= "5a8ec126c297dd9e410eb23685003f670f8f3d57"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.22"
LINUX_VERSION:genericx86-64 = "5.15.22"
LINUX_VERSION:edgerouter = "5.15.1"
LINUX_VERSION:beaglebone-yocto = "5.15.1"
