DESCRIPTION = "Recipe to trigger execution of all meta-multimedia ptest images."
HOMEPAGE = "https://www.openembedded.org/"

LICENSE = "MIT"

inherit features_check nopackages
REQUIRED_DISTRO_FEATURES = "ptest"

require conf/include/ptest-packagelists-meta-multimedia.inc

# Include the full set of ptests
PTESTS_META_MULTIMEDIA = "${PTESTS_FAST_META_MULTIMEDIA} ${PTESTS_SLOW_META_MULTIMEDIA} ${PTESTS_PROBLEMS_META_MULTIMEDIA}"

do_testimage[noexec] = "1"
do_testimage[depends] = "${@' '.join(['meta-multimedia-image-ptest-'+x+':do_testimage' for x in d.getVar('PTESTS_META_MULTIMEDIA').split()])}"

do_build[depends] = "${@' '.join(['meta-multimedia-image-ptest-'+x+':do_build' for x in d.getVar('PTESTS_META_MULTIMEDIA').split()])}"

# normally image.bbclass would do this
EXCLUDE_FROM_WORLD = "1"

python () {
    if bb.utils.contains('IMAGE_CLASSES', 'testimage', True, False, d):
        bb.build.addtask("do_testimage", "", "", d)
}
