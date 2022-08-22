KBRANCH:genericx86  = "v5.10/standard/base"
KBRANCH:genericx86-64  = "v5.10/standard/base"
KBRANCH:edgerouter = "v5.10/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "2883e69e202dc7948c99a7828e192b2b42c2d90a"
SRCREV_machine:genericx86-64 ?= "2883e69e202dc7948c99a7828e192b2b42c2d90a"
SRCREV_machine:edgerouter ?= "7c9332d91089ee63581be6cd3e7197c9d3e9a883"
SRCREV_machine:beaglebone-yocto ?= "3c44f12b9de336579d00ac0105852f4cbf7e8b7d"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.10.130"
LINUX_VERSION:genericx86-64 = "5.10.130"
LINUX_VERSION:edgerouter = "5.10.130"
LINUX_VERSION:beaglebone-yocto = "5.10.130"
