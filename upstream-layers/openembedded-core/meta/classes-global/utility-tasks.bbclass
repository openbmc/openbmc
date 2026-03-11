#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

addtask listtasks
do_listtasks[nostamp] = "1"
python do_listtasks() {
    taskdescs = {}
    maxlen = 0
    for t in bb.build.listtasks(d):
        maxlen = max(maxlen, len(t))

        if t.endswith('_setscene'):
            desc = "%s (setscene version)" % (d.getVarFlag(t[:-9], 'doc') or '')
        else:
            desc = d.getVarFlag(t, 'doc') or ''
        taskdescs[t] = desc

    for task, doc in sorted(taskdescs.items()):
        bb.plain("%s  %s" % (task.ljust(maxlen), doc))
}

CLEANFUNCS ?= ""

T:task-clean = "${LOG_DIR}/cleanlogs/${PN}"
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
do_checkuri[network] = "1"
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


