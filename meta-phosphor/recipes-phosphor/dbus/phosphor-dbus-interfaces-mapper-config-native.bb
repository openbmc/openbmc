SUMMARY="Add /xyz/openbmc_project namespace to phosphor-mapper"
DESCRIPTION="Add the /xyz/openbmc_project path namespace and \
xyz.openbmc_project interface prefix to the mapper \
watch list."

inherit phosphor-mapper
inherit native
inherit obmc-phosphor-license

PHOSPHOR_MAPPER_NAMESPACE_append = " /xyz/openbmc_project"
PHOSPHOR_MAPPER_INTERFACE_append = " xyz.openbmc_project"
