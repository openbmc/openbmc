SLOWTASKS ??= ""
SSTATEVALID ??= ""

def stamptask(d):
    import time

    thistask = d.expand("${PN}:${BB_CURRENTTASK}")
    stampname = d.expand("${TOPDIR}/%s.run" % thistask)
    with open(stampname, "a+") as f:
        f.write(d.getVar("BB_UNIHASH") + "\n")

    if d.getVar("BB_CURRENT_MC") != "default":
        thistask = d.expand("${BB_CURRENT_MC}:${PN}:${BB_CURRENTTASK}")
    if thistask in d.getVar("SLOWTASKS").split():
        bb.note("Slowing task %s" % thistask)
        time.sleep(0.5)
    if d.getVar("BB_HASHSERVE"):
        task = d.getVar("BB_CURRENTTASK")
        if task in ['package', 'package_qa', 'packagedata', 'package_write_ipk', 'package_write_rpm', 'populate_lic', 'populate_sysroot']:
            bb.parse.siggen.report_unihash(os.getcwd(), d.getVar("BB_CURRENTTASK"), d)

    with open(d.expand("${TOPDIR}/task.log"), "a+") as f:
        f.write(thistask + "\n")


def sstate_output_hash(path, sigfile, task, d):
    import hashlib
    h = hashlib.sha256()
    h.update(d.expand("${PN}:${BB_CURRENTTASK}").encode('utf-8'))
    return h.hexdigest()

python do_fetch() {
    # fetch
    stamptask(d)
}
python do_unpack() {
    # unpack
    stamptask(d)
}
python do_patch() {
    # patch
    stamptask(d)
}
python do_populate_lic() {
    # populate_lic
    stamptask(d)
}
python do_prepare_recipe_sysroot() {
    # prepare_recipe_sysroot
    stamptask(d)
}
python do_configure() {
    # configure
    stamptask(d)
}
python do_compile() {
    # compile
    stamptask(d)
}
python do_install() {
    # install
    stamptask(d)
}
python do_populate_sysroot() {
    # populate_sysroot
    stamptask(d)
}
python do_package() {
    # package
    stamptask(d)
}
python do_package_write_ipk() {
    # package_write_ipk
    stamptask(d)
}
python do_package_write_rpm() {
    # package_write_rpm
    stamptask(d)
}
python do_packagedata() {
    # packagedata
    stamptask(d)
}
python do_package_qa() {
    # package_qa
    stamptask(d)
}
python do_build() {
    # build
    stamptask(d)
}
do_prepare_recipe_sysroot[deptask] = "do_populate_sysroot"
do_package[deptask] += "do_packagedata"
do_build[recrdeptask] += "do_deploy"
do_build[recrdeptask] += "do_package_write_ipk"
do_build[recrdeptask] += "do_package_write_rpm"
do_package_qa[rdeptask] = "do_packagedata"
do_populate_lic_deploy[recrdeptask] += "do_populate_lic do_deploy"

DEBIANRDEP = "do_packagedata"
oo_package_write_ipk[rdeptask] = "${DEBIANRDEP}"
do_package_write_rpm[rdeptask] = "${DEBIANRDEP}"

addtask fetch
addtask unpack after do_fetch
addtask patch after do_unpack
addtask prepare_recipe_sysroot after do_patch
addtask configure after do_prepare_recipe_sysroot
addtask compile after do_configure
addtask install after do_compile
addtask populate_sysroot after do_install
addtask package after do_install
addtask package_write_ipk after do_packagedata do_package
addtask package_write_rpm after do_packagedata do_package
addtask packagedata after do_package
addtask package_qa after do_package
addtask build after do_package_qa do_package_write_rpm do_package_write_ipk do_populate_sysroot

python do_package_setscene() {
    stamptask(d)
}
python do_package_qa_setscene() {
    stamptask(d)
}
python do_package_write_ipk_setscene() {
    stamptask(d)
}
python do_package_write_rpm_setscene() {
    stamptask(d)
}
python do_packagedata_setscene() {
    stamptask(d)
}
python do_populate_lic_setscene() {
    stamptask(d)
}
python do_populate_sysroot_setscene() {
    stamptask(d)
}

addtask package_setscene
addtask package_qa_setscene
addtask package_write_ipk_setscene
addtask package_write_rpm_setscene
addtask packagedata_setscene
addtask populate_lic_setscene
addtask populate_sysroot_setscene

BB_SETSCENE_DEPVALID = "setscene_depvalid"

def setscene_depvalid(task, taskdependees, notneeded, d, log=None):
    # taskdependees is a dict of tasks which depend on task, each being a 3 item list of [PN, TASKNAME, FILENAME]
    # task is included in taskdependees too
    # Return - False - We need this dependency
    #        - True - We can skip this dependency
    import re

    def logit(msg, log):
        if log is not None:
            log.append(msg)
        else:
            bb.debug(2, msg)

    logit("Considering setscene task: %s" % (str(taskdependees[task])), log)

    def isNativeCross(x):
        return x.endswith("-native") or "-cross-" in x or "-crosssdk" in x or x.endswith("-cross")

    # We only need to trigger populate_lic through direct dependencies
    if taskdependees[task][1] == "do_populate_lic":
        return True

    # We only need to trigger packagedata through direct dependencies
    # but need to preserve packagedata on packagedata links
    if taskdependees[task][1] == "do_packagedata":
        for dep in taskdependees:
            if taskdependees[dep][1] == "do_packagedata":
                return False
        return True

    for dep in taskdependees:
        logit("  considering dependency: %s" % (str(taskdependees[dep])), log)
        if task == dep:
            continue
        if dep in notneeded:
            continue
        # do_package_write_* and do_package doesn't need do_package
        if taskdependees[task][1] == "do_package" and taskdependees[dep][1] in ['do_package', 'do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package_qa']:
            continue
        # do_package_write_* need do_populate_sysroot as they're mainly postinstall dependencies
        if taskdependees[task][1] == "do_populate_sysroot" and taskdependees[dep][1] in ['do_package_write_ipk', 'do_package_write_rpm']:
            return False
        # do_package/packagedata/package_qa don't need do_populate_sysroot
        if taskdependees[task][1] == "do_populate_sysroot" and taskdependees[dep][1] in ['do_package', 'do_packagedata', 'do_package_qa']:
            continue
        # Native/Cross packages don't exist and are noexec anyway
        if isNativeCross(taskdependees[dep][0]) and taskdependees[dep][1] in ['do_package_write_ipk', 'do_package_write_rpm', 'do_packagedata', 'do_package', 'do_package_qa']:
            continue

        # This is due to the [depends] in useradd.bbclass complicating matters
        # The logic *is* reversed here due to the way hard setscene dependencies are injected
        if (taskdependees[task][1] == 'do_package' or taskdependees[task][1] == 'do_populate_sysroot') and taskdependees[dep][0].endswith(('shadow-native', 'shadow-sysroot', 'base-passwd', 'pseudo-native')) and taskdependees[dep][1] == 'do_populate_sysroot':
            continue

        # Consider sysroot depending on sysroot tasks
        if taskdependees[task][1] == 'do_populate_sysroot' and taskdependees[dep][1] == 'do_populate_sysroot':
            # Native/Cross populate_sysroot need their dependencies
            if isNativeCross(taskdependees[task][0]) and isNativeCross(taskdependees[dep][0]):
                return False
            # Target populate_sysroot depended on by cross tools need to be installed
            if isNativeCross(taskdependees[dep][0]):
                return False
            # Native/cross tools depended upon by target sysroot are not needed
            # Add an exception for shadow-native as required by useradd.bbclass
            if isNativeCross(taskdependees[task][0]) and taskdependees[task][0] != 'shadow-native':
                continue
            # Target populate_sysroot need their dependencies
            return False


        if taskdependees[dep][1] == "do_populate_lic":
            continue

        # Safe fallthrough default
        logit(" Default setscene dependency fall through due to dependency: %s" % (str(taskdependees[dep])), log)
        return False
    return True

BB_HASHCHECK_FUNCTION = "sstate_checkhashes"

def sstate_checkhashes(sq_data, d, siginfo=False, currentcount=0, **kwargs):

    found = set()
    missed = set()

    valid = d.getVar("SSTATEVALID").split()

    for tid in sorted(sq_data['hash']):
        n = os.path.basename(bb.runqueue.fn_from_tid(tid)).split(".")[0] + ":do_" + bb.runqueue.taskname_from_tid(tid)[3:]
        print(n)
        stampfile = d.expand("${TOPDIR}/%s.run" % n.replace("do_", ""))
        if n in valid:
            bb.note("SState: Found valid sstate for %s" % n)
            found.add(tid)
        elif n + ":" + sq_data['hash'][tid] in valid:
            bb.note("SState: Found valid sstate for %s" % n)
            found.add(tid)
        elif os.path.exists(stampfile):
            with open(stampfile, "r") as f:
                hash = f.readline().strip()
            if hash == sq_data['hash'][tid]:
                bb.note("SState: Found valid sstate for %s (already run)" % n)
                found.add(tid)
            else:
                bb.note("SState: sstate hash didn't match previous run for %s (%s vs %s)" % (n, sq_data['hash'][tid], hash))
                missed.add(tid)
        else:
            missed.add(tid)
            bb.note("SState: Found no valid sstate for %s (%s)" % (n, sq_data['hash'][tid]))

    return found

