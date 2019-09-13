SUMMARY = "Phosphor OpenBMC - error log meta data generation"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit native

#To append new recipes that copies error yaml files to the known
#location, add DEPENDS relationhip using bbappend to
#phosphor-logging-error-log-native recipe with the native
#recipe name

#Refer to openpower-debug-collector-error-native.bb to see how
#to copy error yaml files to a known location
