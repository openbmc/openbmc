# Copyright (C) 2006  OpenedHand LTD

# Point to an empty file so any user's custom settings don't break things
QUILTRCFILE ?= "${STAGING_ETCDIR_NATIVE}/quiltrc"

PATCHDEPENDENCY = "${PATCHTOOL}-native:do_populate_sysroot"

inherit terminal

def src_patches(d, all = False ):
    workdir = d.getVar('WORKDIR', True)
    fetch = bb.fetch2.Fetch([], d)
    patches = []
    sources = []
    for url in fetch.urls:
        local = patch_path(url, fetch, workdir)
        if not local:
            if all:
                local = fetch.localpath(url)
                sources.append(local)
            continue

        urldata = fetch.ud[url]
        parm = urldata.parm
        patchname = parm.get('pname') or os.path.basename(local)

        apply, reason = should_apply(parm, d)
        if not apply:
            if reason:
                bb.note("Patch %s %s" % (patchname, reason))
            continue

        patchparm = {'patchname': patchname}
        if "striplevel" in parm:
            striplevel = parm["striplevel"]
        elif "pnum" in parm:
            #bb.msg.warn(None, "Deprecated usage of 'pnum' url parameter in '%s', please use 'striplevel'" % url)
            striplevel = parm["pnum"]
        else:
            striplevel = '1'
        patchparm['striplevel'] = striplevel

        patchdir = parm.get('patchdir')
        if patchdir:
            patchparm['patchdir'] = patchdir

        localurl = bb.fetch.encodeurl(('file', '', local, '', '', patchparm))
        patches.append(localurl)

    if all:
        return sources

    return patches

def patch_path(url, fetch, workdir):
    """Return the local path of a patch, or None if this isn't a patch"""

    local = fetch.localpath(url)
    base, ext = os.path.splitext(os.path.basename(local))
    if ext in ('.gz', '.bz2', '.Z'):
        local = os.path.join(workdir, base)
        ext = os.path.splitext(base)[1]

    urldata = fetch.ud[url]
    if "apply" in urldata.parm:
        apply = oe.types.boolean(urldata.parm["apply"])
        if not apply:
            return
    elif ext not in (".diff", ".patch"):
        return

    return local

def should_apply(parm, d):
    """Determine if we should apply the given patch"""

    if "mindate" in parm or "maxdate" in parm:
        pn = d.getVar('PN', True)
        srcdate = d.getVar('SRCDATE_%s' % pn, True)
        if not srcdate:
            srcdate = d.getVar('SRCDATE', True)

        if srcdate == "now":
            srcdate = d.getVar('DATE', True)

        if "maxdate" in parm and parm["maxdate"] < srcdate:
            return False, 'is outdated'

        if "mindate" in parm and parm["mindate"] > srcdate:
            return False, 'is predated'


    if "minrev" in parm:
        srcrev = d.getVar('SRCREV', True)
        if srcrev and srcrev < parm["minrev"]:
            return False, 'applies to later revisions'

    if "maxrev" in parm:
        srcrev = d.getVar('SRCREV', True)
        if srcrev and srcrev > parm["maxrev"]:
            return False, 'applies to earlier revisions'

    if "rev" in parm:
        srcrev = d.getVar('SRCREV', True)
        if srcrev and parm["rev"] not in srcrev:
            return False, "doesn't apply to revision"

    if "notrev" in parm:
        srcrev = d.getVar('SRCREV', True)
        if srcrev and parm["notrev"] in srcrev:
            return False, "doesn't apply to revision"

    return True, None

should_apply[vardepsexclude] = "DATE SRCDATE"

python patch_do_patch() {
    import oe.patch

    patchsetmap = {
        "patch": oe.patch.PatchTree,
        "quilt": oe.patch.QuiltTree,
        "git": oe.patch.GitApplyTree,
    }

    cls = patchsetmap[d.getVar('PATCHTOOL', True) or 'quilt']

    resolvermap = {
        "noop": oe.patch.NOOPResolver,
        "user": oe.patch.UserResolver,
    }

    rcls = resolvermap[d.getVar('PATCHRESOLVE', True) or 'user']

    classes = {}

    s = d.getVar('S', True)

    path = os.getenv('PATH')
    os.putenv('PATH', d.getVar('PATH', True))

    # We must use one TMPDIR per process so that the "patch" processes
    # don't generate the same temp file name.

    import tempfile
    process_tmpdir = tempfile.mkdtemp()
    os.environ['TMPDIR'] = process_tmpdir

    for patch in src_patches(d):
        _, _, local, _, _, parm = bb.fetch.decodeurl(patch)

        if "patchdir" in parm:
            patchdir = parm["patchdir"]
            if not os.path.isabs(patchdir):
                patchdir = os.path.join(s, patchdir)
        else:
            patchdir = s

        if not patchdir in classes:
            patchset = cls(patchdir, d)
            resolver = rcls(patchset, oe_terminal)
            classes[patchdir] = (patchset, resolver)
            patchset.Clean()
        else:
            patchset, resolver = classes[patchdir]

        bb.note("Applying patch '%s' (%s)" % (parm['patchname'], oe.path.format_display(local, d)))
        try:
            patchset.Import({"file":local, "strippath": parm['striplevel']}, True)
        except Exception as exc:
            bb.utils.remove(process_tmpdir, True)
            bb.fatal(str(exc))
        try:
            resolver.Resolve()
        except bb.BBHandledException as e:
            bb.utils.remove(process_tmpdir, True)
            bb.fatal(str(e))

    bb.utils.remove(process_tmpdir, True)
    del os.environ['TMPDIR']
}
patch_do_patch[vardepsexclude] = "PATCHRESOLVE"

addtask patch after do_unpack
do_patch[dirs] = "${WORKDIR}"
do_patch[depends] = "${PATCHDEPENDENCY}"

EXPORT_FUNCTIONS do_patch
