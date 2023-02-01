KBRANCH:genericx86  = "v6.1/standard/base"
KBRANCH:genericx86-64  = "v6.1/standard/base"
KBRANCH:edgerouter = "v6.1/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v6.1/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "872afe79c5e568acf5f971952e78caada8424df7"
SRCREV_machine:genericx86-64 ?= "872afe79c5e568acf5f971952e78caada8424df7"
SRCREV_machine:edgerouter ?= "872afe79c5e568acf5f971952e78caada8424df7"
SRCREV_machine:beaglebone-yocto ?= "872afe79c5e568acf5f971952e78caada8424df7"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "6.1.3"
LINUX_VERSION:genericx86-64 = "6.1.3"
LINUX_VERSION:edgerouter = "6.1.3"
LINUX_VERSION:beaglebone-yocto = "6.1.3"
