KBRANCH:genericx86  = "v5.13/standard/base"
KBRANCH:genericx86-64  = "v5.13/standard/base"
KBRANCH:edgerouter = "v5.13/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.13/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "fe64083abac67ac736aa0133f3a4088286aece40"
SRCREV_machine:genericx86-64 ?= "fe64083abac67ac736aa0133f3a4088286aece40"
SRCREV_machine:edgerouter ?= "7b80606f7484fb1967a261e7e262de9adeb7ed59"
SRCREV_machine:beaglebone-yocto ?= "e486ea86794d62e7e6adbb3a2b2fd65222f323f7"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.13.11"
LINUX_VERSION:genericx86-64 = "5.13.11"
LINUX_VERSION:edgerouter = "5.13.11"
LINUX_VERSION:beaglebone-yocto = "5.13.11"
