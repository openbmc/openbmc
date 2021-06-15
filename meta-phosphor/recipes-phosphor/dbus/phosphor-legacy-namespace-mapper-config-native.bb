SUMMARY="Add org.openbmc namespace to phosphor-mapper"
DESCRIPTION="Add the legacy org.openbmc service namespace and \
org.openbmc interface prefix to the mapper watch list."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " org.openbmc"
PHOSPHOR_MAPPER_INTERFACE_append = " org.openbmc"
