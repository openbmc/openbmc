KBRANCH:genericx86  = "v6.6/standard/base"
KBRANCH:genericx86-64  = "v6.6/standard/base"
KBRANCH:beaglebone-yocto = "v6.6/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "332d4668fcc32826907d4f3c4938845206006089"
SRCREV_machine:genericx86-64 ?= "332d4668fcc32826907d4f3c4938845206006089"
SRCREV_machine:beaglebone-yocto ?= "332d4668fcc32826907d4f3c4938845206006089"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "6.6.15"
LINUX_VERSION:genericx86-64 = "6.6.15"
LINUX_VERSION:beaglebone-yocto = "6.6.15"
