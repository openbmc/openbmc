SUMMARY="Add /org/open_power namespace to phosphor-mapper"
DESCRIPTION="Add the /org/open_power path namespace and \
org.open_power interface prefix to the mapper \
watch list."

inherit phosphor-mapper
inherit native
inherit obmc-phosphor-license

PHOSPHOR_MAPPER_NAMESPACE_append = " /org/open_power"
PHOSPHOR_MAPPER_INTERFACE_append = " org.open_power"
