SUMMARY = "Phosphor OpenBMC - error log meta data generation"
PR = "r1"

inherit native
inherit obmc-phosphor-license

#To append new recipes that copies error yaml files to the known
#location, add DEPENDS relationhip using bbappend to
#phosphor-logging-error-log-native recipe with the native
#recipe name

#Refer to openpower-debug-collector-error-native.bb to see how
#to copy error yaml files to a known location
