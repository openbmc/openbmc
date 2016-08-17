# Version v0.96
PV = "0.96"
SRCREV = "24f8c0175e04bab78e7555698e34d7002b01c87f"

SRC_URI = "git://git.kernel.org/pub/scm/utils/rt-tests/rt-tests.git"

S = "${WORKDIR}/git"

# need to append rt-tests' default CFLAGS to ours
CFLAGS += "-I${S}/src/include -D_GNU_SOURCE -Wall -Wno-nonnull"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"
