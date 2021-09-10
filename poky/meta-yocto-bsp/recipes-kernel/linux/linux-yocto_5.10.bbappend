KBRANCH:genericx86  = "v5.10/standard/base"
KBRANCH:genericx86-64  = "v5.10/standard/base"
KBRANCH:edgerouter = "v5.10/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "c274623910704eefcc98380a17649889ac7e9408"
SRCREV_machine:genericx86-64 ?= "c274623910704eefcc98380a17649889ac7e9408"
SRCREV_machine:edgerouter ?= "ac089d661362ba857e235c5630242039b150ae26"
SRCREV_machine:beaglebone-yocto ?= "a6df693a45f5787d4254e0998f52b4465b2a5efe"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.10.55"
LINUX_VERSION:genericx86-64 = "5.10.55"
LINUX_VERSION:edgerouter = "5.10.55"
LINUX_VERSION:beaglebone-yocto = "5.10.55"
