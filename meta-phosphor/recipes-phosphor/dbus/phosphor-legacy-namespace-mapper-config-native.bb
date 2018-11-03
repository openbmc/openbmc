SUMMARY="Add org.openbmc namespace to phosphor-mapper"
DESCRIPTION="Add the legacy org.openbmc service namespace and \
org.openbmc interface prefix to the mapper watch list."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " org.openbmc"
PHOSPHOR_MAPPER_INTERFACE_append = " org.openbmc"
