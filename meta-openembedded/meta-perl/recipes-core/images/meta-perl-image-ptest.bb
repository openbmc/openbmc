inherit features_check
REQUIRED_DISTRO_FEATURES += "ptest"

SUMMARY = "meta-perl build ptest image"

require recipes-core/images/core-image-minimal.bb
require conf/include/ptest-packagelists-meta-perl.inc

DESCRIPTION += "Also including the ${MCNAME} ptest package."
HOMEPAGE = "https://www.openembedded.org/"

PTESTS_META_PERL = "${PTESTS_SLOW_META_PERL} ${PTESTS_FAST_META_PERL}"

IMAGE_INSTALL:append = " ${MCNAME}-ptest openssh"

BBCLASSEXTEND = "${@' '.join(['mcextend:'+x for x in d.getVar('PTESTS_META_PERL').split()])}"

# The image can be sufficiently large (~1.8GB) that we need to be careful that it fits in a live
# image (which has a 4GB limit), so nullify the overhead factor (1.3x out of the
# box) and explicitly add up to 1500MB.
IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "324288"
# If a particular ptest needs more space, it can be customized:
#IMAGE_ROOTFS_EXTRA_SPACE:virtclass-mcextend-<pn> = "1024288"

# ptests need more memory than standard to avoid the OOM killer
QB_MEM = "-m 1024"
# If a particular ptest needs more memory, it can be customized:
#QB_MEM:virtclass-mcextend-<pn> = "-m 4096"

TEST_SUITES = "ping ssh parselogs ptest"

# Sadly at the moment the full set of ptests is not robust enough and sporadically fails in random places
PTEST_EXPECT_FAILURE = "1"

python () {
    if not d.getVar("MCNAME"):
        raise bb.parse.SkipRecipe("No class extension set")
}

