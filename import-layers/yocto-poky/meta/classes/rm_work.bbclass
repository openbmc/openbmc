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

# Use the completion scheduler by default when rm_work is active
# to try and reduce disk usage
BB_SCHEDULER ?= "completion"

RMWORK_ORIG_TASK := "${BB_DEFAULT_TASK}"
BB_DEFAULT_TASK = "rm_work_all"

do_rm_work () {
    # If the recipe name is in the RM_WORK_EXCLUDE, skip the recipe.
    for p in ${RM_WORK_EXCLUDE}; do
        if [ "$p" = "${PN}" ]; then
            bbnote "rm_work: Skipping ${PN} since it is in RM_WORK_EXCLUDE"
            exit 0
        fi
    done

    cd ${WORKDIR}
    for dir in *
    do
        # Retain only logs and other files in temp, safely ignore
        # failures of removing pseudo folers on NFS2/3 server.
        if [ $dir = 'pseudo' ]; then
            rm -rf $dir 2> /dev/null || true
        elif [ $dir != 'temp' ]; then
            rm -rf $dir
        fi
    done

    # Need to add pseudo back or subsqeuent work in this workdir
    # might fail since setscene may not rerun to recreate it
    mkdir -p ${WORKDIR}/pseudo/

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
            *sigdata*)
                i=dummy
                break
                ;;
            *do_package_write*)
                i=dummy
                break
                ;;
            *do_rootfs*)
               i=dummy
               break
               ;;
            *do_image*)
               i=dummy
               break
               ;;
            *do_build*)
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
}
addtask rm_work after do_${RMWORK_ORIG_TASK}

do_rm_work_all () {
    :
}
do_rm_work_all[recrdeptask] = "do_rm_work"
addtask rm_work_all after do_rm_work

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

python () {
    # If the recipe name is in the RM_WORK_EXCLUDE, skip the recipe.
    excludes = (d.getVar("RM_WORK_EXCLUDE", True) or "").split()
    pn = d.getVar("PN", True)
    if pn in excludes:
        d.delVarFlag('rm_work_rootfs', 'cleandirs')
        d.delVarFlag('rm_work_populatesdk', 'cleandirs')
}
