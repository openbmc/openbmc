KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "1275299b4a49c5845378537d2d623dfbe027dcca"
SRCREV_machine:genericx86-64 ?= "1275299b4a49c5845378537d2d623dfbe027dcca"
SRCREV_machine:edgerouter ?= "28658152bb865c3e7ffde6ac277fab5dc1940c0a"
SRCREV_machine:beaglebone-yocto ?= "4f8b81b735ff381cde5ae840552727175393b77a"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.80"
LINUX_VERSION:genericx86-64 = "5.15.80"
LINUX_VERSION:edgerouter = "5.15.80"
LINUX_VERSION:beaglebone-yocto = "5.15.80"
