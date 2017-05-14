SUMMARY = "Phosphor OpenBMC - eror log meta data generation"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

#To append new recipes that copies error yaml files to the shared
#directories, add DEPENDS relationhip to phosphor-logging-error-log.bbappend
#with the native recipe provider name

#shared directories are specified in phosphor-logging.bbclass

#For example refer to phosphor-logging-error-log.bbappend
#which has a openpower debug collector native recipe added 
#DEPENDS += "virtual/openpower-debug-collector-error"

deltask populate_sysroot
