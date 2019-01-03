SUMMARY="Add /com/intel namespace to phosphor-mapper"
DESCRIPTION="Add the /com/intel path namespace and \
com.intel interface prefix to the mapper watch list."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${INTELBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " com.intel"
PHOSPHOR_MAPPER_INTERFACE_append = " com.intel"
