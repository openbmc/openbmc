SUMMARY = "Phosphor OpenBMC - error log meta data generation"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native

#To append new recipes that copies error yaml files to the known
#location, add DEPENDS relationhip using bbappend to
#phosphor-logging-error-log-native recipe with the native
#recipe name

#Refer to openpower-debug-collector-error-native.bb to see how
#to copy error yaml files to a known location
