BUILDSTATS_BASE = "${TMPDIR}/buildstats/"

################################################################################
# Build statistics gathering.
#
# The CPU and Time gathering/tracking functions and bbevent inspiration
# were written by Christopher Larson.
#
################################################################################

def get_buildprocess_cputime(pid):
    with open("/proc/%d/stat" % pid, "r") as f:
        fields = f.readline().rstrip().split()
    # 13: utime, 14: stime, 15: cutime, 16: cstime
    return sum(int(field) for field in fields[13:16])

def get_process_cputime(pid):
    import resource
    with open("/proc/%d/stat" % pid, "r") as f:
        fields = f.readline().rstrip().split()
    stats = { 
        'utime'  : fields[13],
        'stime'  : fields[14], 
        'cutime' : fields[15], 
        'cstime' : fields[16],  
    }
    iostats = {}
    if os.path.isfile("/proc/%d/io" % pid):
        with open("/proc/%d/io" % pid, "r") as f:
            while True:
                i = f.readline().strip()
                if not i:
                    break
                if not ":" in i:
                    # one more extra line is appended (empty or containing "0")
                    # most probably due to race condition in kernel while
                    # updating IO stats
                    break
                i = i.split(": ")
                iostats[i[0]] = i[1]
    resources = resource.getrusage(resource.RUSAGE_SELF)
    childres = resource.getrusage(resource.RUSAGE_CHILDREN)
    return stats, iostats, resources, childres

def get_cputime():
    with open("/proc/stat", "r") as f:
        fields = f.readline().rstrip().split()[1:]
    return sum(int(field) for field in fields)

def set_timedata(var, d, server_time):
    d.setVar(var, server_time)

def get_timedata(var, d, end_time):
    oldtime = d.getVar(var, False)
    if oldtime is None:
        return
    return end_time - oldtime

def set_buildtimedata(var, d):
    import time
    time = time.time()
    cputime = get_cputime()
    proctime = get_buildprocess_cputime(os.getpid())
    d.setVar(var, (time, cputime, proctime))

def get_buildtimedata(var, d):
    import time
    timedata = d.getVar(var, False)
    if timedata is None:
        return
    oldtime, oldcpu, oldproc = timedata
    procdiff = get_buildprocess_cputime(os.getpid()) - oldproc
    cpudiff = get_cputime() - oldcpu
    end_time = time.time()
    timediff = end_time - oldtime
    if cpudiff > 0:
        cpuperc = float(procdiff) * 100 / cpudiff
    else:
        cpuperc = None
    return timediff, cpuperc

def write_task_data(status, logfile, e, d):
    with open(os.path.join(logfile), "a") as f:
        elapsedtime = get_timedata("__timedata_task", d, e.time)
        if elapsedtime:
            f.write(d.expand("${PF}: %s\n" % e.task))
            f.write(d.expand("Elapsed time: %0.2f seconds\n" % elapsedtime))
            cpu, iostats, resources, childres = get_process_cputime(os.getpid())
            if cpu:
                f.write("utime: %s\n" % cpu['utime'])
                f.write("stime: %s\n" % cpu['stime'])
                f.write("cutime: %s\n" % cpu['cutime'])
                f.write("cstime: %s\n" % cpu['cstime'])
            for i in iostats:
                f.write("IO %s: %s\n" % (i, iostats[i]))
            rusages = ["ru_utime", "ru_stime", "ru_maxrss", "ru_minflt", "ru_majflt", "ru_inblock", "ru_oublock", "ru_nvcsw", "ru_nivcsw"]
            for i in rusages:
                f.write("rusage %s: %s\n" % (i, getattr(resources, i)))
            for i in rusages:
                f.write("Child rusage %s: %s\n" % (i, getattr(childres, i)))
        if status == "passed":
            f.write("Status: PASSED \n")
        else:
            f.write("Status: FAILED \n")
        f.write("Ended: %0.2f \n" % e.time)

def write_host_data(logfile, e, d, type):
    import subprocess, os, datetime
    # minimum time allowed for each command to run, in seconds
    time_threshold = 0.5
    limit = 10
    # the total number of commands
    num_cmds = 0
    msg = ""
    if type == "interval":
        # interval at which data will be logged
        interval = d.getVar("BB_HEARTBEAT_EVENT", False)
        if interval is None:
            bb.warn("buildstats: Collecting host data at intervals failed. Set BB_HEARTBEAT_EVENT=\"<interval>\" in conf/local.conf for the interval at which host data will be logged.")
            d.setVar("BB_LOG_HOST_STAT_ON_INTERVAL", "0")
            return
        interval = int(interval)
        cmds = d.getVar('BB_LOG_HOST_STAT_CMDS_INTERVAL')
        msg = "Host Stats: Collecting data at %d second intervals.\n" % interval
        if cmds is None:
            d.setVar("BB_LOG_HOST_STAT_ON_INTERVAL", "0")
            bb.warn("buildstats: Collecting host data at intervals failed. Set BB_LOG_HOST_STAT_CMDS_INTERVAL=\"command1 ; command2 ; ... \" in conf/local.conf.")
            return
    if type == "failure":
        cmds = d.getVar('BB_LOG_HOST_STAT_CMDS_FAILURE')
        msg = "Host Stats: Collecting data on failure.\n"
        msg += "Failed at task: " + e.task + "\n"
        if cmds is None:
            d.setVar("BB_LOG_HOST_STAT_ON_FAILURE", "0")
            bb.warn("buildstats: Collecting host data on failure failed. Set BB_LOG_HOST_STAT_CMDS_FAILURE=\"command1 ; command2 ; ... \" in conf/local.conf.")
            return
    c_san = []
    for cmd in cmds.split(";"):
        if len(cmd) == 0:
            continue
        num_cmds += 1
        c_san.append(cmd)
    if num_cmds == 0:
        if type == "interval":
            d.setVar("BB_LOG_HOST_STAT_ON_INTERVAL", "0")
        if type == "failure":
            d.setVar("BB_LOG_HOST_STAT_ON_FAILURE", "0")
        return

    # return if the interval is not enough to run all commands within the specified BB_HEARTBEAT_EVENT interval
    if type == "interval":
        limit = interval / num_cmds
        if limit <= time_threshold:
            d.setVar("BB_LOG_HOST_STAT_ON_INTERVAL", "0")
            bb.warn("buildstats: Collecting host data failed. BB_HEARTBEAT_EVENT interval not enough to run the specified commands. Increase value of BB_HEARTBEAT_EVENT in conf/local.conf.")
            return

    # set the environment variables 
    path = d.getVar("PATH")
    opath = d.getVar("BB_ORIGENV", False).getVar("PATH")
    ospath = os.environ['PATH']
    os.environ['PATH'] = path + ":" + opath + ":" + ospath
    with open(logfile, "a") as f:
        f.write("Event Time: %f\nDate: %s\n" % (e.time, datetime.datetime.now()))
        f.write("%s" % msg)
        for c in c_san:
            try:
                output = subprocess.check_output(c.split(), stderr=subprocess.STDOUT, timeout=limit).decode('utf-8')
            except (subprocess.CalledProcessError, subprocess.TimeoutExpired, FileNotFoundError) as err:
                output = "Error running command: %s\n%s\n" % (c, err)
            f.write("%s\n%s\n" % (c, output))
    # reset the environment
    os.environ['PATH'] = ospath

python run_buildstats () {
    import bb.build
    import bb.event
    import time, subprocess, platform

    bn = d.getVar('BUILDNAME')
    ########################################################################
    # bitbake fires HeartbeatEvent even before a build has been
    # triggered, causing BUILDNAME to be None
    ########################################################################
    if bn is not None:
        bsdir = os.path.join(d.getVar('BUILDSTATS_BASE'), bn)
        taskdir = os.path.join(bsdir, d.getVar('PF'))
        if isinstance(e, bb.event.HeartbeatEvent) and bb.utils.to_boolean(d.getVar("BB_LOG_HOST_STAT_ON_INTERVAL")):
            bb.utils.mkdirhier(bsdir)
            write_host_data(os.path.join(bsdir, "host_stats_interval"), e, d, "interval")

    if isinstance(e, bb.event.BuildStarted):
        ########################################################################
        # If the kernel was not configured to provide I/O statistics, issue
        # a one time warning.
        ########################################################################
        if not os.path.isfile("/proc/%d/io" % os.getpid()):
            bb.warn("The Linux kernel on your build host was not configured to provide process I/O statistics. (CONFIG_TASK_IO_ACCOUNTING is not set)")

        ########################################################################
        # at first pass make the buildstats hierarchy and then
        # set the buildname
        ########################################################################
        bb.utils.mkdirhier(bsdir)
        set_buildtimedata("__timedata_build", d)
        build_time = os.path.join(bsdir, "build_stats")
        # write start of build into build_time
        with open(build_time, "a") as f:
            host_info = platform.uname()
            f.write("Host Info: ")
            for x in host_info:
                if x:
                    f.write(x + " ")
            f.write("\n")
            f.write("Build Started: %0.2f \n" % d.getVar('__timedata_build', False)[0])

    elif isinstance(e, bb.event.BuildCompleted):
        build_time = os.path.join(bsdir, "build_stats")
        with open(build_time, "a") as f:
            ########################################################################
            # Write build statistics for the build
            ########################################################################
            timedata = get_buildtimedata("__timedata_build", d)
            if timedata:
                time, cpu = timedata
                # write end of build and cpu used into build_time
                f.write("Elapsed time: %0.2f seconds \n" % (time))
                if cpu:
                    f.write("CPU usage: %0.1f%% \n" % cpu)

    if isinstance(e, bb.build.TaskStarted):
        set_timedata("__timedata_task", d, e.time)
        bb.utils.mkdirhier(taskdir)
        # write into the task event file the name and start time
        with open(os.path.join(taskdir, e.task), "a") as f:
            f.write("Event: %s \n" % bb.event.getName(e))
            f.write("Started: %0.2f \n" % e.time)

    elif isinstance(e, bb.build.TaskSucceeded):
        write_task_data("passed", os.path.join(taskdir, e.task), e, d)
        if e.task == "do_rootfs":
            bs = os.path.join(bsdir, "build_stats")
            with open(bs, "a") as f:
                rootfs = d.getVar('IMAGE_ROOTFS')
                if os.path.isdir(rootfs):
                    try:
                        rootfs_size = subprocess.check_output(["du", "-sh", rootfs],
                                stderr=subprocess.STDOUT).decode('utf-8')
                        f.write("Uncompressed Rootfs size: %s" % rootfs_size)
                    except subprocess.CalledProcessError as err:
                        bb.warn("Failed to get rootfs size: %s" % err.output.decode('utf-8'))

    elif isinstance(e, bb.build.TaskFailed):
        # Can have a failure before TaskStarted so need to mkdir here too
        bb.utils.mkdirhier(taskdir)
        write_task_data("failed", os.path.join(taskdir, e.task), e, d)
        ########################################################################
        # Lets make things easier and tell people where the build failed in
        # build_status. We do this here because BuildCompleted triggers no
        # matter what the status of the build actually is
        ########################################################################
        build_status = os.path.join(bsdir, "build_stats")
        with open(build_status, "a") as f:
            f.write(d.expand("Failed at: ${PF} at task: %s \n" % e.task))
        if bb.utils.to_boolean(d.getVar("BB_LOG_HOST_STAT_ON_FAILURE")):
            write_host_data(os.path.join(bsdir, "host_stats_%s_failure" % e.task), e, d, "failure")
}

addhandler run_buildstats
run_buildstats[eventmask] = "bb.event.BuildStarted bb.event.BuildCompleted bb.event.HeartbeatEvent bb.build.TaskStarted bb.build.TaskSucceeded bb.build.TaskFailed"

python runqueue_stats () {
    import buildstats
    from bb import event, runqueue
    # We should not record any samples before the first task has started,
    # because that's the first activity shown in the process chart.
    # Besides, at that point we are sure that the build variables
    # are available that we need to find the output directory.
    # The persistent SystemStats is stored in the datastore and
    # closed when the build is done.
    system_stats = d.getVar('_buildstats_system_stats', False)
    if not system_stats and isinstance(e, (bb.runqueue.sceneQueueTaskStarted, bb.runqueue.runQueueTaskStarted)):
        system_stats = buildstats.SystemStats(d)
        d.setVar('_buildstats_system_stats', system_stats)
    if system_stats:
        # Ensure that we sample at important events.
        done = isinstance(e, bb.event.BuildCompleted)
        system_stats.sample(e, force=done)
        if done:
            system_stats.close()
            d.delVar('_buildstats_system_stats')
}

addhandler runqueue_stats
runqueue_stats[eventmask] = "bb.runqueue.sceneQueueTaskStarted bb.runqueue.runQueueTaskStarted bb.event.HeartbeatEvent bb.event.BuildCompleted bb.event.MonitorDiskEvent"
