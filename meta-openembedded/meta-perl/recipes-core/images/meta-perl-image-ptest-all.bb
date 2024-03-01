DESCRIPTION = "Recipe to trigger execution of all meta-perl ptest images."
HOMEPAGE = "https://www.openembedded.org/"

LICENSE = "MIT"

inherit features_check nopackages
REQUIRED_DISTRO_FEATURES = "ptest"

require conf/include/ptest-packagelists-meta-perl.inc

# Include the full set of ptests
PTESTS_META_PERL = "${PTESTS_FAST_META_PERL} ${PTESTS_SLOW_META_PERL} ${PTESTS_PROBLEMS_META_PERL}"

do_testimage[noexec] = "1"
do_testimage[depends] = "${@' '.join(['meta-perl-image-ptest-'+x+':do_testimage' for x in d.getVar('PTESTS_META_PERL').split()])}"

do_build[depends] = "${@' '.join(['meta-perl-image-ptest-'+x+':do_build' for x in d.getVar('PTESTS_META_PERL').split()])}"

# normally image.bbclass would do this
EXCLUDE_FROM_WORLD = "1"

python () {
    if bb.utils.contains('IMAGE_CLASSES', 'testimage', True, False, d):
        bb.build.addtask("do_testimage", "", "", d)
}
