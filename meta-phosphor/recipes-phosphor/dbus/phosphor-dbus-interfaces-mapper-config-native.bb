SUMMARY="Add xyz.openbmc_project namespace to phosphor-mapper"
DESCRIPTION="Add the xyz.openbmc_project service namespace and \
xyz.openbmc_project interface prefix to the mapper \
watch list."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " xyz.openbmc_project"
PHOSPHOR_MAPPER_INTERFACE_append = " xyz.openbmc_project org.freedesktop.DBus.ObjectManager"
