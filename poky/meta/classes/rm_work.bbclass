#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# Removes source after build
#
# To use it add that line to conf/local.conf:
#
# INHERIT += "rm_work"
#
# To inhibit rm_work for some recipes, specify them in RM_WORK_EXCLUDE.
# For example, in conf/local.conf:
#
# RM_WORK_EXCLUDE += "icu-native icu busybox"
#
# Recipes can also configure which entries in their ${WORKDIR}
# are preserved besides temp, which already gets excluded by default
# because it contains logs:
# do_install:append () {
#     echo "bar" >${WORKDIR}/foo
# }
# RM_WORK_EXCLUDE_ITEMS += "foo"
RM_WORK_EXCLUDE_ITEMS = "temp"

# Use the completion scheduler by default when rm_work is active
# to try and reduce disk usage
BB_SCHEDULER ?= "completion"

# Run the rm_work task in the idle scheduling class
BB_TASK_IONICE_LEVEL:task-rm_work = "3.0"

do_rm_work () {
    # Force using the HOSTTOOLS 'rm' - otherwise the SYSROOT_NATIVE 'rm' can be selected depending on PATH
    # Avoids race-condition accessing 'rm' when deleting WORKDIR folders at the end of this function
    RM_BIN="$(PATH=${HOSTTOOLS_DIR} command -v rm)"
    if [ -z "${RM_BIN}" ]; then
        bbfatal "Binary 'rm' not found in HOSTTOOLS_DIR, cannot remove WORKDIR data."
    fi

    # If the recipe name is in the RM_WORK_EXCLUDE, skip the recipe.
    for p in ${RM_WORK_EXCLUDE}; do
        if [ "$p" = "${PN}" ]; then
            bbnote "rm_work: Skipping ${PN} since it is in RM_WORK_EXCLUDE"
            exit 0
        fi
    done

    # Need to add pseudo back or subsqeuent work in this workdir
    # might fail since setscene may not rerun to recreate it
    mkdir -p ${WORKDIR}/pseudo/

    excludes='${RM_WORK_EXCLUDE_ITEMS}'

    # Change normal stamps into setscene stamps as they better reflect the
    # fact WORKDIR is now empty
    # Also leave noexec stamps since setscene stamps don't cover them
    STAMPDIR=`dirname ${STAMP}`
    if test -d $STAMPDIR; then
        cd $STAMPDIR
        for i in `basename ${STAMP}`*
        do
            case $i in
            *sigdata*|*sigbasedata*)
                # Save/skip anything that looks like a signature data file.
                ;;
            *do_image_complete_setscene*|*do_image_qa_setscene*)
                # Ensure we don't 'stack' setscene extensions to these stamps with the sections below
                ;;
            *do_image_complete*)
                # Promote do_image_complete stamps to setscene versions (ahead of *do_image* below)
                mv $i `echo $i | sed -e "s#do_image_complete#do_image_complete_setscene#"`
                ;;
            *do_image_qa*)
                # Promote do_image_qa stamps to setscene versions (ahead of *do_image* below)
                mv $i `echo $i | sed -e "s#do_image_qa#do_image_qa_setscene#"`
                ;;
            *do_package_write*|*do_rootfs*|*do_image*|*do_bootimg*|*do_write_qemuboot_conf*|*do_build*)
                ;;
            *do_addto_recipe_sysroot*)
                # Preserve recipe-sysroot-native if do_addto_recipe_sysroot has been used
                excludes="$excludes recipe-sysroot-native"
                ;;
            *do_package|*do_package.*|*do_package_setscene.*)
                # We remove do_package entirely, including any
                # sstate version since otherwise we'd need to leave 'plaindirs' around
                # such as 'packages' and 'packages-split' and these can be large. No end
                # of chain tasks depend directly on do_package anymore.
                "${RM_BIN}" -f -- $i;
                ;;
            *_setscene*)
                # Skip stamps which are already setscene versions
                ;;
            *)
                # For everything else: if suitable, promote the stamp to a setscene
                # version, otherwise remove it
                for j in ${SSTATETASKS} do_shared_workdir
                do
                    case $i in
                    *$j|*$j.*)
                        mv $i `echo $i | sed -e "s#${j}#${j}_setscene#"`
                        break
                        ;;
                    esac
                done
                "${RM_BIN}" -f -- $i
            esac
        done
    fi

    cd ${WORKDIR}
    for dir in *
    do
        # Retain only logs and other files in temp, safely ignore
        # failures of removing pseudo folers on NFS2/3 server.
        if [ $dir = 'pseudo' ]; then
            "${RM_BIN}" -rf -- $dir 2> /dev/null || true
        elif ! echo "$excludes" | grep -q -w "$dir"; then
            "${RM_BIN}" -rf -- $dir
        fi
    done
}
do_rm_work[vardepsexclude] += "SSTATETASKS"

do_rm_work_all () {
    :
}
do_rm_work_all[recrdeptask] = "do_rm_work"
do_rm_work_all[noexec] = "1"
addtask rm_work_all before do_build

do_populate_sdk[postfuncs] += "rm_work_populatesdk"
rm_work_populatesdk () {
    :
}
rm_work_populatesdk[cleandirs] = "${WORKDIR}/sdk"

do_image_complete[postfuncs] += "rm_work_rootfs"
rm_work_rootfs () {
    :
}
rm_work_rootfs[cleandirs] = "${WORKDIR}/rootfs"

# This task can be used instead of do_build to trigger building
# without also invoking do_rm_work. It only exists when rm_work.bbclass
# is active, otherwise do_build needs to be used.
#
# The intended usage is
# ${@ d.getVar('RM_WORK_BUILD_WITHOUT') or 'do_build'}
# in places that previously used just 'do_build'.
RM_WORK_BUILD_WITHOUT = "do_build_without_rm_work"
do_build_without_rm_work () {
    :
}
do_build_without_rm_work[noexec] = "1"

# We have to add these tasks already now, because all tasks are
# meant to be defined before the RecipeTaskPreProcess event triggers.
# The inject_rm_work event handler then merely changes task dependencies.
addtask do_rm_work
addtask do_build_without_rm_work
addhandler inject_rm_work
inject_rm_work[eventmask] = "bb.event.RecipeTaskPreProcess"
python inject_rm_work() {
    if bb.data.inherits_class('kernel', d):
        d.appendVar("RM_WORK_EXCLUDE", ' ' + d.getVar("PN"))
    # If the recipe name is in the RM_WORK_EXCLUDE, skip the recipe.
    excludes = (d.getVar("RM_WORK_EXCLUDE") or "").split()
    pn = d.getVar("PN")

    # Determine what do_build depends upon, without including do_build
    # itself or our own special do_rm_work_all.
    deps = sorted((set(bb.build.preceedtask('do_build', True, d))).difference(('do_build', 'do_rm_work_all')) or "")

    # deps can be empty if do_build doesn't exist, e.g. *-inital recipes
    if not deps:
        deps = ["do_populate_sysroot", "do_populate_lic"]

    if pn in excludes:
        d.delVarFlag('rm_work_rootfs', 'cleandirs')
        d.delVarFlag('rm_work_populatesdk', 'cleandirs')
    else:
        # Inject do_rm_work into the tasks of the current recipe such that do_build
        # depends on it and that it runs after all other tasks that block do_build,
        # i.e. after all work on the current recipe is done. The reason for taking
        # this approach instead of making do_rm_work depend on do_build is that
        # do_build inherits additional runtime dependencies on
        # other recipes and thus will typically run much later than completion of
        # work in the recipe itself.
        # In practice, addtask() here merely updates the dependencies.
        bb.build.addtask('do_rm_work', 'do_rm_work_all do_build', ' '.join(deps), d)

    # Always update do_build_without_rm_work dependencies.
    bb.build.addtask('do_build_without_rm_work', '', ' '.join(deps), d)
}
