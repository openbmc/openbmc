KBRANCH:genericx86  = "standard/base"
KBRANCH:genericx86-64  = "standard/base"
KBRANCH:beaglebone-yocto = "standard/beaglebone"

KMACHINE:genericarm64 ?= "genericarm64"
KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"
