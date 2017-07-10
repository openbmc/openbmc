# Add the legacy /org/openbmc path namespace
# and legacy org.openbmc interface prefix to the
# mapper watch list.

inherit phosphor-mapper
inherit native
inherit obmc-phosphor-license

PHOSPHOR_MAPPER_NAMESPACE_append = " /org/openbmc"
PHOSPHOR_MAPPER_INTERFACE_append = " org.openbmc"
