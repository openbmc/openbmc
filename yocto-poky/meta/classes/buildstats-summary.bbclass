# Summarize sstate usage at the end of the build
python buildstats_summary () {
    import collections
    import os.path

    bsdir = e.data.expand("${BUILDSTATS_BASE}/${BUILDNAME}")
    if not os.path.exists(bsdir):
        return

    sstatetasks = (e.data.getVar('SSTATETASKS', True) or '').split()
    built = collections.defaultdict(lambda: [set(), set()])
    for pf in os.listdir(bsdir):
        taskdir = os.path.join(bsdir, pf)
        if not os.path.isdir(taskdir):
            continue

        tasks = os.listdir(taskdir)
        for t in sstatetasks:
            no_sstate, sstate = built[t]
            if t in tasks:
                no_sstate.add(pf)
            elif t + '_setscene' in tasks:
                sstate.add(pf)

    header_printed = False
    for t in sstatetasks:
        no_sstate, sstate = built[t]
        if no_sstate | sstate:
            if not header_printed:
                header_printed = True
                bb.note("Build completion summary:")

            bb.note("  {0}: {1}% sstate reuse ({2} setscene, {3} scratch)".format(t, 100*len(sstate)/(len(sstate)+len(no_sstate)), len(sstate), len(no_sstate)))
}
addhandler buildstats_summary
buildstats_summary[eventmask] = "bb.event.BuildCompleted"
