#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Summarize sstate usage at the end of the build
python buildstats_summary () {
    import collections
    import os.path

    bsdir = e.data.expand("${BUILDSTATS_BASE}/${BUILDNAME}")
    if not os.path.exists(bsdir):
        return

    sstatetasks = (e.data.getVar('SSTATETASKS') or '').split()
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

            sstate_count = len(sstate)
            no_sstate_count = len(no_sstate)
            total_count = sstate_count + no_sstate_count
            bb.note("  {0}: {1:.1f}% sstate reuse({2} setscene, {3} scratch)".format(
                t, round(100 * sstate_count / total_count, 1), sstate_count, no_sstate_count))
}
addhandler buildstats_summary
buildstats_summary[eventmask] = "bb.event.BuildCompleted"
