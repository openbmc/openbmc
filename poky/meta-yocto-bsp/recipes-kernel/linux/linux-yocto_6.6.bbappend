KBRANCH:genericx86  = "v6.6/standard/base"
KBRANCH:genericx86-64  = "v6.6/standard/base"
KBRANCH:beaglebone-yocto = "v6.6/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "195b2994f955071be3dd16ff61127dbc6b2e0069"
SRCREV_machine:genericx86-64 ?= "195b2994f955071be3dd16ff61127dbc6b2e0069"
SRCREV_machine:beaglebone-yocto ?= "195b2994f955071be3dd16ff61127dbc6b2e0069"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "6.6.12"
LINUX_VERSION:genericx86-64 = "6.6.12"
LINUX_VERSION:beaglebone-yocto = "6.6.12"
