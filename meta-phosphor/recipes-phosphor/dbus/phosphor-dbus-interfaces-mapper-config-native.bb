SUMMARY="Add xyz.openbmc_project namespace to phosphor-mapper"
DESCRIPTION="Add the xyz.openbmc_project service namespace and \
xyz.openbmc_project interface prefix to the mapper \
watch list."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " xyz.openbmc_project"
PHOSPHOR_MAPPER_INTERFACE_append = " xyz.openbmc_project org.freedesktop.DBus.ObjectManager"
