#
# Collects debug information in order to create error report files.
#
# Copyright (C) 2013 Intel Corporation
# Author: Andreea Brandusa Proca <andreea.b.proca@intel.com>
#
# Licensed under the MIT license, see COPYING.MIT for details

ERR_REPORT_DIR ?= "${LOG_DIR}/error-report"

def errorreport_getdata(e):
    import codecs
    logpath = e.data.getVar('ERR_REPORT_DIR', True)
    datafile = os.path.join(logpath, "error-report.txt")
    with codecs.open(datafile, 'r', 'utf-8') as f:
        data = f.read()
    return data

def errorreport_savedata(e, newdata, file):
    import json
    import codecs
    logpath = e.data.getVar('ERR_REPORT_DIR', True)
    datafile = os.path.join(logpath, file)
    with codecs.open(datafile, 'w', 'utf-8') as f:
        json.dump(newdata, f, indent=4, sort_keys=True)
    return datafile

python errorreport_handler () {
        import json
        import codecs

        logpath = e.data.getVar('ERR_REPORT_DIR', True)
        datafile = os.path.join(logpath, "error-report.txt")

        if isinstance(e, bb.event.BuildStarted):
            bb.utils.mkdirhier(logpath)
            data = {}
            machine = e.data.getVar("MACHINE", True)
            data['machine'] = machine
            data['build_sys'] = e.data.getVar("BUILD_SYS", True)
            data['nativelsb'] = e.data.getVar("NATIVELSBSTRING", True)
            data['distro'] = e.data.getVar("DISTRO", True)
            data['target_sys'] = e.data.getVar("TARGET_SYS", True)
            data['failures'] = []
            data['component'] = " ".join(e.getPkgs())
            data['branch_commit'] = str(base_detect_branch(e.data)) + ": " + str(base_detect_revision(e.data))
            lock = bb.utils.lockfile(datafile + '.lock')
            errorreport_savedata(e, data, "error-report.txt")
            bb.utils.unlockfile(lock)

        elif isinstance(e, bb.build.TaskFailed):
            task = e.task
            taskdata={}
            log = e.data.getVar('BB_LOGFILE', True)
            taskdata['package'] = e.data.expand("${PF}")
            taskdata['task'] = task
            if log:
                try:
                    logFile = codecs.open(log, 'r', 'utf-8')
                    logdata = logFile.read()

                    # Replace host-specific paths so the logs are cleaner
                    for d in ("TOPDIR", "TMPDIR"):
                        s = e.data.getVar(d, True)
                        if s:
                            logdata = logdata.replace(s, d)

                    logFile.close()
                except:
                    logdata = "Unable to read log file"

            else:
                logdata = "No Log"

            # server will refuse failures longer than param specified in project.settings.py
            # MAX_UPLOAD_SIZE = "5242880"
            # use lower value, because 650 chars can be spent in task, package, version
            max_logdata_size = 5242000
            # upload last max_logdata_size characters
            if len(logdata) > max_logdata_size:
                logdata = "..." + logdata[-max_logdata_size:]
            taskdata['log'] = logdata
            lock = bb.utils.lockfile(datafile + '.lock')
            jsondata = json.loads(errorreport_getdata(e))
            jsondata['failures'].append(taskdata)
            errorreport_savedata(e, jsondata, "error-report.txt")
            bb.utils.unlockfile(lock)

        elif isinstance(e, bb.event.BuildCompleted):
            lock = bb.utils.lockfile(datafile + '.lock')
            jsondata = json.loads(errorreport_getdata(e))
            bb.utils.unlockfile(lock)
            failures = jsondata['failures']
            if(len(failures) > 0):
                filename = "error_report_" + e.data.getVar("BUILDNAME", True)+".txt"
                datafile = errorreport_savedata(e, jsondata, filename)
                bb.note("The errors for this build are stored in %s\nYou can send the errors to a reports server by running:\n  send-error-report %s [-s server]" % (datafile, datafile))
                bb.note("The contents of these logs will be posted in public if you use the above command with the default server. Please ensure you remove any identifying or proprietary information when prompted before sending.")
}

addhandler errorreport_handler
errorreport_handler[eventmask] = "bb.event.BuildStarted bb.event.BuildCompleted bb.build.TaskFailed"
