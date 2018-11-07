SUMMARY="Add /org/open_power namespace to phosphor-mapper"
DESCRIPTION="Add the /org/open_power path namespace and \
org.open_power interface prefix to the mapper \
watch list."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " org.open_power"
PHOSPHOR_MAPPER_INTERFACE_append = " org.open_power"
