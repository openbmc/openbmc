KBRANCH:genericx86  = "v5.10/standard/base"
KBRANCH:genericx86-64  = "v5.10/standard/base"
KBRANCH:edgerouter = "v5.10/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "4f2bb635ea267e71f112fd11323c1d3a2f2b85d0"
SRCREV_machine:genericx86-64 ?= "4f2bb635ea267e71f112fd11323c1d3a2f2b85d0"
SRCREV_machine:edgerouter ?= "4ab94e777d8b41ee1ee4c279259e9733bc8049b1"
SRCREV_machine:beaglebone-yocto ?= "941cc9c3849f96f7eaf109b1e35e05ba366aca56"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.10.87"
LINUX_VERSION:genericx86-64 = "5.10.87"
LINUX_VERSION:edgerouter = "5.10.63"
LINUX_VERSION:beaglebone-yocto = "5.10.63"
