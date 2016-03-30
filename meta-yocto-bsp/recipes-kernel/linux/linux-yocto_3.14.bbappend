KBRANCH_genericx86  = "standard/common-pc/base"
KBRANCH_genericx86-64  = "standard/common-pc-64/base"
KBRANCH_edgerouter = "standard/edgerouter"
KBRANCH_beaglebone = "standard/beaglebone"
KBRANCH_mpc8315e-rdb = "standard/fsl-mpc8315e-rdb"

KMACHINE_genericx86 ?= "common-pc"
KMACHINE_genericx86-64 ?= "common-pc-64"

SRCREV_machine_genericx86 ?= "d9bf859dfae6f88b88b157119c20ae4d5e51420a"
SRCREV_machine_genericx86-64 ?= "93b2b800d85c1565af7d96f3776dc38c85ae1902"
SRCREV_machine_edgerouter ?= "578602a722dbfb260801f3b37c6eafd2abb2340d"
SRCREV_machine_beaglebone ?= "578602a722dbfb260801f3b37c6eafd2abb2340d"
SRCREV_machine_mpc8315e-rdb ?= "1cb1bbaf63cecc918cf36c89819a7464af4c4b13"

COMPATIBLE_MACHINE_genericx86 = "genericx86"
COMPATIBLE_MACHINE_genericx86-64 = "genericx86-64"
COMPATIBLE_MACHINE_edgerouter = "edgerouter"
COMPATIBLE_MACHINE_beaglebone = "beaglebone"
COMPATIBLE_MACHINE_mpc8315e-rdb = "mpc8315e-rdb"

LINUX_VERSION_genericx86 = "3.14.39"
LINUX_VERSION_genericx86-64 = "3.14.39"
