# Version 1.1
SRCREV = "dff174f994f547a5785d32454865f140daacb0f5"
PE = "1"

SRC_URI = "git://git.kernel.org/pub/scm/utils/rt-tests/rt-tests.git"
# 1.2 and 1.3 seem to be development versions
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(?!1\.2)(?!1\.3)(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"
