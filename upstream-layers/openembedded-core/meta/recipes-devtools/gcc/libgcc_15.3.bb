require recipes-devtools/gcc/gcc-${PV}.inc
require libgcc.inc

LDFLAGS += "-fuse-ld=bfd"

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET:armv6 = "arm"

# remove at next version upgrade or when output changes
DESCRIPTION += " (libgcc)"
HASHEQUIV_HASH_VERSION .= ".1"
