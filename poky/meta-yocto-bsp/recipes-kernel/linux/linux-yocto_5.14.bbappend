KBRANCH:genericx86  = "v5.14/standard/base"
KBRANCH:genericx86-64  = "v5.14/standard/base"
KBRANCH:edgerouter = "v5.14/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.14/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:genericx86-64 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:edgerouter ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:beaglebone-yocto ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.14.6"
LINUX_VERSION:genericx86-64 = "5.14.6"
LINUX_VERSION:edgerouter = "5.14.6"
LINUX_VERSION:beaglebone-yocto = "5.14.6"
