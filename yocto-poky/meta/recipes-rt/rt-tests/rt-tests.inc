# Version v0.92
PV = "0.92"
SRCREV = "5f9f1e3fe327440a9d405f4af8feb16ff7a909eb"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/clrkwllms/rt-tests.git \
           file://0001-rt-tests-Allow-for-user-specified-PYLIB.patch \
           file://0002-rt-tests-Break-out-install_hwlatdetect.patch \
           file://hackbench-fix-error-check.patch \
           file://Makefile-fix-gzip-command.patch \
           file://fix-re-install-failure.patch \
"

S = "${WORKDIR}/git"

# need to append rt-tests' default CFLAGS to ours
CFLAGS += "-I${S}/src/include -D_GNU_SOURCE -Wall -Wno-nonnull"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"
