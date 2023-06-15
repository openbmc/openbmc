KBRANCH:genericx86  = "v5.15/standard/base"
KBRANCH:genericx86-64  = "v5.15/standard/base"
KBRANCH:edgerouter = "v5.15/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.15/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "0b628306d1f9ea28c0e86369ce9bb87a47893c9c"
SRCREV_machine:genericx86-64 ?= "0b628306d1f9ea28c0e86369ce9bb87a47893c9c"
SRCREV_machine:edgerouter ?= "90f1ee6589264545f548d731c2480b08a007230f"
SRCREV_machine:beaglebone-yocto ?= "9aabbaa89fcb21af7028e814c1f5b61171314d5a"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.15.72"
LINUX_VERSION:genericx86-64 = "5.15.72"
LINUX_VERSION:edgerouter = "5.15.54"
LINUX_VERSION:beaglebone-yocto = "5.15.54"
