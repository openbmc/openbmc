KBRANCH:genericx86  = "v5.10/standard/base"
KBRANCH:genericx86-64  = "v5.10/standard/base"
KBRANCH:edgerouter = "v5.10/standard/edgerouter"
KBRANCH:beaglebone-yocto = "v5.10/standard/beaglebone"

KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"
KMACHINE:beaglebone-yocto ?= "beaglebone"

SRCREV_machine:genericx86 ?= "a8b4c628f382412e5e7df5750f2be711df95fa06"
SRCREV_machine:genericx86-64 ?= "a8b4c628f382412e5e7df5750f2be711df95fa06"
SRCREV_machine:edgerouter ?= "43577894d2295a92fce760dc403b97527fb55835"
SRCREV_machine:beaglebone-yocto ?= "8038166b729c192d06f1eb37ab6868a5769f8bc5"

COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE:edgerouter = "edgerouter"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"

LINUX_VERSION:genericx86 = "5.10.113"
LINUX_VERSION:genericx86-64 = "5.10.113"
LINUX_VERSION:edgerouter = "5.10.113"
LINUX_VERSION:beaglebone-yocto = "5.10.113"
