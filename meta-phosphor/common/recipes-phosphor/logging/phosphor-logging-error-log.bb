SUMMARY = "Phosphor OpenBMC - eror log file generation"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

#To append new recipes that has error yaml files, create an
#native recipe that copies error yaml files to a shared folder.
#Add the native recipe provider name to the
#phosphor-logging-error-log.bbappend file

deltask populate_sysroot
