#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python_pep517 python3native python3-dir setuptools3-base

DEPENDS += "python3-hatchling-native"

# delete nested, empty directories from the python site-packages path. Make
# sure that we remove the native ones for target builds as well
hatchling_rm_emptydirs:class-target () {
        find ${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/* -depth -type d -empty -delete
        find ${STAGING_LIBDIR_NATIVE}/${PYTHON_DIR}/site-packages/* -depth -type d -empty -delete
}

hatchling_rm_emptydirs:class-native () {
        find ${STAGING_LIBDIR_NATIVE}/${PYTHON_DIR}/site-packages/* -depth -type d -empty -delete
}

# Define a default empty version of hatchling_rm_emptydirs to appease bitbake
hatchling_rm_emptydirs () {
        :
}

do_prepare_recipe_sysroot[postfuncs] += " hatchling_rm_emptydirs"
