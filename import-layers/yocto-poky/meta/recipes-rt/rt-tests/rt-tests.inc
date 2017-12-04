# Version 1.1
SRCREV = "dff174f994f547a5785d32454865f140daacb0f5"
PE = "1"

SRC_URI = "git://git.kernel.org/pub/scm/utils/rt-tests/rt-tests.git"

S = "${WORKDIR}/git"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"
