PRSERV_DUMPDIR ??= "${LOG_DIR}/db"
LOCALCOUNT_DUMPFILE ??= "${PRSERV_DUMPDIR}/prserv-localcount-exports.inc"

python migrate_localcount_handler () {
    import bb.event
    if not e.data:
        return

    pv = e.data.getVar('PV', True)
    if not 'AUTOINC' in pv:
        return

    localcounts = bb.persist_data.persist('BB_URI_LOCALCOUNT', e.data)
    pn = e.data.getVar('PN', True)
    revs = localcounts.get_by_pattern('%%-%s_rev' % pn)
    counts = localcounts.get_by_pattern('%%-%s_count' % pn)
    if not revs or not counts:
        return

    if len(revs) != len(counts):
        bb.warn("The number of revs and localcounts don't match in %s" % pn)
        return

    version = e.data.getVar('PRAUTOINX', True)
    srcrev = bb.fetch2.get_srcrev(e.data)
    base_ver = 'AUTOINC-%s' % version[:version.find(srcrev)]
    pkgarch = e.data.getVar('PACKAGE_ARCH', True)
    value = max(int(count) for count in counts)

    if len(revs) == 1:
        if srcrev != ('AUTOINC+%s' % revs[0]):
            value += 1
    else:
        value += 1

    bb.utils.mkdirhier(e.data.getVar('PRSERV_DUMPDIR', True))
    df = e.data.getVar('LOCALCOUNT_DUMPFILE', True)
    flock = bb.utils.lockfile("%s.lock" % df)
    with open(df, 'a') as fd:
        fd.write('PRAUTO$%s$%s$%s = "%s"\n' %
                (base_ver, pkgarch, srcrev, str(value)))
    bb.utils.unlockfile(flock)
}

addhandler migrate_localcount_handler
migrate_localcount_handler[eventmask] = "bb.event.RecipeParsed"
