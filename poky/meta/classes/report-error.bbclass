#
# Collects debug information in order to create error report files.
#
# Copyright (C) 2013 Intel Corporation
# Author: Andreea Brandusa Proca <andreea.b.proca@intel.com>
#
# Licensed under the MIT license, see COPYING.MIT for details

inherit base

ERR_REPORT_DIR ?= "${LOG_DIR}/error-report"

def errorreport_getdata(e):
    import codecs
    logpath = e.data.getVar('ERR_REPORT_DIR')
    datafile = os.path.join(logpath, "error-report.txt")
    with codecs.open(datafile, 'r', 'utf-8') as f:
        data = f.read()
    return data

def errorreport_savedata(e, newdata, file):
    import json
    import codecs
    logpath = e.data.getVar('ERR_REPORT_DIR')
    datafile = os.path.join(logpath, file)
    with codecs.open(datafile, 'w', 'utf-8') as f:
        json.dump(newdata, f, indent=4, sort_keys=True)
    return datafile

def get_conf_data(e, filename):
    builddir = e.data.getVar('TOPDIR')
    filepath = os.path.join(builddir, "conf", filename)
    jsonstring = ""
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            for line in f.readlines():
                if line.startswith("#") or len(line.strip()) == 0:
                    continue
                else:
                    jsonstring=jsonstring + line
    return jsonstring

python errorreport_handler () {
        import json
        import codecs

        def nativelsb():
            nativelsbstr = e.data.getVar("NATIVELSBSTRING")
            # provide a bit more host info in case of uninative build
            if e.data.getVar('UNINATIVE_URL') != 'unset':
                return '/'.join([nativelsbstr, lsb_distro_identifier(e.data)])
            return nativelsbstr

        logpath = e.data.getVar('ERR_REPORT_DIR')
        datafile = os.path.join(logpath, "error-report.txt")

        if isinstance(e, bb.event.BuildStarted):
            bb.utils.mkdirhier(logpath)
            data = {}
            machine = e.data.getVar("MACHINE")
            data['machine'] = machine
            data['build_sys'] = e.data.getVar("BUILD_SYS")
            data['nativelsb'] = nativelsb()
            data['distro'] = e.data.getVar("DISTRO")
            data['target_sys'] = e.data.getVar("TARGET_SYS")
            data['failures'] = []
            data['component'] = " ".join(e.getPkgs())
            data['branch_commit'] = str(base_detect_branch(e.data)) + ": " + str(base_detect_revision(e.data))
            data['bitbake_version'] = e.data.getVar("BB_VERSION")
            data['layer_version'] = get_layers_branch_rev(e.data)
            data['local_conf'] = get_conf_data(e, 'local.conf')
            data['auto_conf'] = get_conf_data(e, 'auto.conf')
            lock = bb.utils.lockfile(datafile + '.lock')
            errorreport_savedata(e, data, "error-report.txt")
            bb.utils.unlockfile(lock)

        elif isinstance(e, bb.build.TaskFailed):
            task = e.task
            taskdata={}
            log = e.data.getVar('BB_LOGFILE')
            taskdata['package'] = e.data.expand("${PF}")
            taskdata['task'] = task
            if log:
                try:
                    with codecs.open(log, encoding='utf-8') as logFile:
                        logdata = logFile.read()
                    # Replace host-specific paths so the logs are cleaner
                    for d in ("TOPDIR", "TMPDIR"):
                        s = e.data.getVar(d)
                        if s:
                            logdata = logdata.replace(s, d)
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
                filename = "error_report_" + e.data.getVar("BUILDNAME")+".txt"
                datafile = errorreport_savedata(e, jsondata, filename)
                bb.note("The errors for this build are stored in %s\nYou can send the errors to a reports server by running:\n  send-error-report %s [-s server]" % (datafile, datafile))
                bb.note("The contents of these logs will be posted in public if you use the above command with the default server. Please ensure you remove any identifying or proprietary information when prompted before sending.")
}

addhandler errorreport_handler
errorreport_handler[eventmask] = "bb.event.BuildStarted bb.event.BuildCompleted bb.build.TaskFailed"
