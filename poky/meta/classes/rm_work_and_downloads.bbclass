# Author:       Patrick Ohly <patrick.ohly@intel.com>
# Copyright:    Copyright (C) 2015 Intel Corporation
#
# This file is licensed under the MIT license, see COPYING.MIT in
# this source distribution for the terms.

# This class is used like rm_work:
# INHERIT += "rm_work_and_downloads"
#
# In addition to removing local build directories of a recipe, it also
# removes the downloaded source. This is achieved by making the DL_DIR
# recipe-specific. While reducing disk usage, it increases network usage (for
# example, compiling the same source for target and host implies downloading
# the source twice).
#
# Because the "do_fetch" task does not get re-run after removing the downloaded
# sources, this class is also not suitable for incremental builds.
#
# Where it works well is in well-connected build environments with limited
# disk space (like TravisCI).

inherit rm_work

# This would ensure that the existing do_rm_work() removes the downloads,
# but does not work because some recipes have a circular dependency between
# WORKDIR and DL_DIR (via ${SRCPV}?).
# DL_DIR = "${WORKDIR}/downloads"

# Instead go up one level and remove ourself.
DL_DIR = "${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}/${PN}/downloads"
do_rm_work_append () {
    rm -rf ${DL_DIR}
}
