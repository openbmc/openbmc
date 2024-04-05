COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

KBRANCH:genericx86  = "v6.6/standard/base"
KBRANCH:genericx86-64  = "v6.6/standard/base"
KBRANCH:beaglebone-yocto = "v6.6/standard/beaglebone"

KMACHINE:genericarm64 ?= "genericarm64"
KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "06644f0d7193d7ec39d7fe41939a21953e7a0c65"
SRCREV_machine:genericx86-64 ?= "06644f0d7193d7ec39d7fe41939a21953e7a0c65"
SRCREV_machine:beaglebone-yocto ?= "06644f0d7193d7ec39d7fe41939a21953e7a0c65"

LINUX_VERSION:genericx86 = "6.6.21"
LINUX_VERSION:genericx86-64 = "6.6.21"
LINUX_VERSION:beaglebone-yocto = "6.6.21"
