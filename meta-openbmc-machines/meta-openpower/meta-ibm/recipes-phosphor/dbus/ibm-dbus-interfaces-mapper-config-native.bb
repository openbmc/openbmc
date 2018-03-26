SUMMARY="Add /com/ibm namespace to phosphor-mapper"
DESCRIPTION="Add the /com/ibm path namespace and \
com.ibm interface prefix to the mapper watch list."

inherit phosphor-mapper
inherit native
inherit obmc-phosphor-license

PHOSPHOR_MAPPER_NAMESPACE_append = " /com/ibm"
PHOSPHOR_MAPPER_INTERFACE_append = " com.ibm"
