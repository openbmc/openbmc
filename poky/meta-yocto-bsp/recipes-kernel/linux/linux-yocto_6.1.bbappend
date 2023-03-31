KBRANCH:genericx86  = "v6.1/standard/base"
KBRANCH:genericx86-64  = "v6.1/standard/base"
KBRANCH:edgerouter = "v6.1/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v6.1/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "423e1996694b61fbfc8ec3bf062fc6461d64fde1"
SRCREV_machine:genericx86-64 ?= "423e1996694b61fbfc8ec3bf062fc6461d64fde1"
SRCREV_machine:edgerouter ?= "423e1996694b61fbfc8ec3bf062fc6461d64fde1"
SRCREV_machine:beaglebone-yocto ?= "423e1996694b61fbfc8ec3bf062fc6461d64fde1"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "6.1.20"
LINUX_VERSION:genericx86-64 = "6.1.20"
LINUX_VERSION:edgerouter = "6.1.20"
LINUX_VERSION:beaglebone-yocto = "6.1.20"
