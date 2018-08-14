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
# do_install_append () {
#     echo "bar" >${WORKDIR}/foo
# }
# RM_WORK_EXCLUDE_ITEMS += "foo"
RM_WORK_EXCLUDE_ITEMS = "temp"

# Use the completion scheduler by default when rm_work is active
# to try and reduce disk usage
BB_SCHEDULER ?= "completion"

# Run the rm_work task in the idle scheduling class
BB_TASK_IONICE_LEVEL_task-rm_work = "3.0"

do_rm_work () {
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
    cd `dirname ${STAMP}`
    for i in `basename ${STAMP}`*
    do
        for j in ${SSTATETASKS} do_shared_workdir
        do
            case $i in
            *do_setscene*)
                break
                ;;
            *sigdata*|*sigbasedata*)
                i=dummy
                break
                ;;
            *do_package_write*)
                i=dummy
                break
                ;;
            *do_image_complete*)
                mv $i `echo $i | sed -e "s#do_image_complete#do_image_complete_setscene#"`
                i=dummy
                break
                ;;
            *do_rootfs*|*do_image*|*do_bootimg*|*do_write_qemuboot_conf*)
                i=dummy
                break
                ;;
            *do_build*)
                i=dummy
                break
                ;;
            *do_addto_recipe_sysroot*)
                # Preserve recipe-sysroot-native if do_addto_recipe_sysroot has been used
                excludes="$excludes recipe-sysroot-native"
                i=dummy
                break
                ;;
            # We remove do_package entirely, including any
            # sstate version since otherwise we'd need to leave 'plaindirs' around
            # such as 'packages' and 'packages-split' and these can be large. No end
            # of chain tasks depend directly on do_package anymore.
            *do_package|*do_package.*|*do_package_setscene.*)
                rm -f $i;
                i=dummy
                break
                ;;
            *_setscene*)
                i=dummy
                break
                ;;
            *$j|*$j.*)
                mv $i `echo $i | sed -e "s#${j}#${j}_setscene#"`
                i=dummy
                break
            ;;
            esac
        done
        rm -f $i
    done

    cd ${WORKDIR}
    for dir in *
    do
        # Retain only logs and other files in temp, safely ignore
        # failures of removing pseudo folers on NFS2/3 server.
        if [ $dir = 'pseudo' ]; then
            rm -rf $dir 2> /dev/null || true
        elif ! echo "$excludes" | grep -q -w "$dir"; then
            rm -rf $dir
        fi
    done
}
do_rm_work_all () {
    :
}
do_rm_work_all[recrdeptask] = "do_rm_work"
do_rm_work_all[noexec] = "1"
addtask rm_work_all after before do_build

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
    deps = set(bb.build.preceedtask('do_build', True, d))
    deps.difference_update(('do_build', 'do_rm_work_all'))

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
        bb.build.addtask('do_rm_work', 'do_build', ' '.join(deps), d)

    # Always update do_build_without_rm_work dependencies.
    bb.build.addtask('do_build_without_rm_work', '', ' '.join(deps), d)
}
