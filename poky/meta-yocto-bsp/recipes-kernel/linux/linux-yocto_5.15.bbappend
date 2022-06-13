KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "ebfb1822e9f9726d8c587fc0f60cfed43fa0873e"
SRCREV_machine:genericx86-64 ?= "ebfb1822e9f9726d8c587fc0f60cfed43fa0873e"
SRCREV_machine:edgerouter ?= "b978686694c3e41968821d6cc2a2a371fd9c2fb0"
SRCREV_machine:beaglebone-yocto ?= "4c875cf1376178dfab4913aa1350cab50bb093d3"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.36"
LINUX_VERSION:genericx86-64 = "5.15.36"
LINUX_VERSION:edgerouter = "5.15.36"
LINUX_VERSION:beaglebone-yocto = "5.15.36"
