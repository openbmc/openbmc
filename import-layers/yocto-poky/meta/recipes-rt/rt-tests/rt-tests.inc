# Version v0.96
SRCREV = "e1b1537a20b35af75a49bf55dcf70296f8a62467"

SRC_URI = "git://git.kernel.org/pub/scm/utils/rt-tests/rt-tests.git"

S = "${WORKDIR}/git"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"
