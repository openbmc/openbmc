addtask listtasks
do_listtasks[nostamp] = "1"
python do_listtasks() {
    taskdescs = {}
    maxlen = 0
    for e in d.keys():
        if d.getVarFlag(e, 'task'):
            maxlen = max(maxlen, len(e))
            if e.endswith('_setscene'):
                desc = "%s (setscene version)" % (d.getVarFlag(e[:-9], 'doc') or '')
            else:
                desc = d.getVarFlag(e, 'doc') or ''
            taskdescs[e] = desc

    tasks = sorted(taskdescs.keys())
    for taskname in tasks:
        bb.plain("%s  %s" % (taskname.ljust(maxlen), taskdescs[taskname]))
}

CLEANFUNCS ?= ""

T_task-clean = "${LOG_DIR}/cleanlogs/${PN}"
addtask clean
do_clean[nostamp] = "1"
python do_clean() {
    """clear the build and temp directories"""
    dir = d.expand("${WORKDIR}")
    bb.note("Removing " + dir)
    oe.path.remove(dir)

    dir = "%s.*" % d.getVar('STAMP')
    bb.note("Removing " + dir)
    oe.path.remove(dir)

    for f in (d.getVar('CLEANFUNCS') or '').split():
        bb.build.exec_func(f, d)
}

addtask checkuri
do_checkuri[nostamp] = "1"
python do_checkuri() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        fetcher.checkstatus()
    except bb.fetch2.BBFetchException as e:
        bb.fatal(str(e))
}

addtask checkuriall after do_checkuri
do_checkuriall[recrdeptask] = "do_checkuriall do_checkuri"
do_checkuriall[recideptask] = "do_${BB_DEFAULT_TASK}"
do_checkuriall[nostamp] = "1"
do_checkuriall() {
	:
}

addtask fetchall after do_fetch
do_fetchall[recrdeptask] = "do_fetchall do_fetch"
do_fetchall[recideptask] = "do_${BB_DEFAULT_TASK}"
do_fetchall() {
	:
}
