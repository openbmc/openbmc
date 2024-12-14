inherit features_check
REQUIRED_DISTRO_FEATURES = "ptest"

require core-image-minimal.bb
require conf/distro/include/ptest-packagelists.inc

DESCRIPTION += "Also including the ${MCNAME} ptest package."
SUMMARY ?= "${MCNAME} ptest image."
HOMEPAGE = "https://www.yoctoproject.org/"

PTESTS = "${PTESTS_SLOW} ${PTESTS_FAST}"

IMAGE_INSTALL:append = " ${MCNAME}-ptest openssh"

BBCLASSEXTEND = "${@' '.join(['mcextend:'+x for x in d.getVar('PTESTS').split()])}"

# The image can sufficiently large (~1.8GB) that we need to be careful that it fits in a live
# image (which has a 4GB limit), so nullify the overhead factor (1.3x out of the
# box) and explicitly add up to 1500MB.
# strace-ptest in particular needs more than 500MB
IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "324288"
IMAGE_ROOTFS_EXTRA_SPACE:virtclass-mcextend-mdadm = "1524288"
IMAGE_ROOTFS_EXTRA_SPACE:virtclass-mcextend-strace = "1524288"
IMAGE_ROOTFS_EXTRA_SPACE:virtclass-mcextend-lttng-tools = "1524288"

# tar-ptest in particular needs more space
IMAGE_ROOTFS_EXTRA_SPACE:virtclass-mcextend-tar = "1524288"

# ptests need more memory than standard to avoid the OOM killer
QB_MEM = "-m 1024"
QB_MEM:virtclass-mcextend-lttng-tools = "-m 4096"
QB_MEM:virtclass-mcextend-python3 = "-m 2048"
QB_MEM:virtclass-mcextend-python3-cryptography = "-m 5100"
QB_MEM:virtclass-mcextend-tcl = "-m 5100"

TEST_SUITES = "ping ssh parselogs ptest"

# Sadly at the moment the full set of ptests is not robust enough and sporadically fails in random places
PTEST_EXPECT_FAILURE = "1"

python () {
    if not d.getVar("MCNAME"):
        raise bb.parse.SkipRecipe("No class extension set")
}
