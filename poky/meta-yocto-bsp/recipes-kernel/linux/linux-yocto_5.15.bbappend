KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "686a68a75222a0fadc1f829334596b02534dcb42"
SRCREV_machine:genericx86-64 ?= "686a68a75222a0fadc1f829334596b02534dcb42"
SRCREV_machine:edgerouter ?= "2ac6461adfceb54f47a756046fbdd142adce4301"
SRCREV_machine:beaglebone-yocto ?= "1aebe207c2559fb88da6d622f2964a0a15a4e6e3"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.106"
LINUX_VERSION:genericx86-64 = "5.15.106"
LINUX_VERSION:edgerouter = "5.15.103"
LINUX_VERSION:beaglebone-yocto = "5.15.106"
