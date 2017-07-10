SUMMARY="Add /org/openbmc namespace to phosphor-mapper"
DESCRIPTION="Add the legacy /org/openbmc path namespace and \
org.openbmc nterface prefix to the mapper watch list."

inherit phosphor-mapper
inherit native
inherit obmc-phosphor-license

PHOSPHOR_MAPPER_NAMESPACE_append = " /org/openbmc"
PHOSPHOR_MAPPER_INTERFACE_append = " org.openbmc"
