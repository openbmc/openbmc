SUMMARY="Add /com/ibm namespace to phosphor-mapper"
DESCRIPTION="Add the /com/ibm path namespace and \
com.ibm interface prefix to the mapper watch list."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " com.ibm"
PHOSPHOR_MAPPER_INTERFACE_append = " com.ibm"
