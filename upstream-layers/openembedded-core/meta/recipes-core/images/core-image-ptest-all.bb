SUMMARY = "Recipe to trigger execution of all ptest images."
HOMEPAGE = "https://www.yoctoproject.org/"

LICENSE = "MIT"

inherit features_check nopackages
REQUIRED_DISTRO_FEATURES = "ptest"

require conf/distro/include/ptest-packagelists.inc

# Include the full set of ptests
PTESTS = "${PTESTS_FAST} ${PTESTS_SLOW}"

do_testimage[noexec] = "1"
do_testimage[depends] = "${@' '.join(['core-image-ptest-'+x+':do_testimage' for x in d.getVar('PTESTS').split()])}"

do_build[depends] = "${@' '.join(['core-image-ptest-'+x+':do_build' for x in d.getVar('PTESTS').split()])}"

# normally image.bbclass would do this
EXCLUDE_FROM_WORLD = "1"

python () {
    if bb.utils.contains('IMAGE_CLASSES', 'testimage', True, False, d):
        bb.build.addtask("do_testimage", "", "", d)
}
