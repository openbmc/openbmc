KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "024d08fb706170a9723e9751e505681f9d4c7ab6"
SRCREV_machine:genericx86-64 ?= "024d08fb706170a9723e9751e505681f9d4c7ab6"
SRCREV_machine:edgerouter ?= "2ac6461adfceb54f47a756046fbdd142adce4301"
SRCREV_machine:beaglebone-yocto ?= "26aee42556a000123129552b73de6bf2ac039034"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.103"
LINUX_VERSION:genericx86-64 = "5.15.103"
LINUX_VERSION:edgerouter = "5.15.103"
LINUX_VERSION:beaglebone-yocto = "5.15.103"
