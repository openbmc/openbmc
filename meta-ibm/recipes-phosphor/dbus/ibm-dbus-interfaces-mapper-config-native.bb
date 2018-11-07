SUMMARY="Add /com/ibm namespace to phosphor-mapper"
DESCRIPTION="Add the /com/ibm path namespace and \
com.ibm interface prefix to the mapper watch list."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit phosphor-mapper
inherit native

PHOSPHOR_MAPPER_SERVICE_append = " com.ibm"
PHOSPHOR_MAPPER_INTERFACE_append = " com.ibm"
