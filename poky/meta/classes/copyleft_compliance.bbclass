# Deploy sources for recipes for compliance with copyleft-style licenses
# Defaults to using symlinks, as it's a quick operation, and one can easily
# follow the links when making use of the files (e.g. tar with the -h arg).
#
# vi:sts=4:sw=4:et

inherit copyleft_filter

COPYLEFT_SOURCES_DIR ?= '${DEPLOY_DIR}/copyleft_sources'

python do_prepare_copyleft_sources () {
    """Populate a tree of the recipe sources and emit patch series files"""
    import os.path
    import shutil

    p = d.getVar('P')
    included, reason = copyleft_should_include(d)
    if not included:
        bb.debug(1, 'copyleft: %s is excluded: %s' % (p, reason))
        return
    else:
        bb.debug(1, 'copyleft: %s is included: %s' % (p, reason))

    sources_dir = d.getVar('COPYLEFT_SOURCES_DIR')
    dl_dir = d.getVar('DL_DIR')
    src_uri = d.getVar('SRC_URI').split()
    fetch = bb.fetch2.Fetch(src_uri, d)
    ud = fetch.ud

    pf = d.getVar('PF')
    dest = os.path.join(sources_dir, pf)
    shutil.rmtree(dest, ignore_errors=True)
    bb.utils.mkdirhier(dest)

    for u in ud.values():
        local = os.path.normpath(fetch.localpath(u.url))
        if local.endswith('.bb'):
            continue
        elif local.endswith('/'):
            local = local[:-1]

        if u.mirrortarball:
            tarball_path = os.path.join(dl_dir, u.mirrortarball)
            if os.path.exists(tarball_path):
                local = tarball_path

        oe.path.symlink(local, os.path.join(dest, os.path.basename(local)), force=True)

    patches = src_patches(d)
    for patch in patches:
        _, _, local, _, _, parm = bb.fetch.decodeurl(patch)
        patchdir = parm.get('patchdir')
        if patchdir:
            series = os.path.join(dest, 'series.subdir.%s' % patchdir.replace('/', '_'))
        else:
            series = os.path.join(dest, 'series')

        with open(series, 'a') as s:
            s.write('%s -p%s\n' % (os.path.basename(local), parm['striplevel']))
}

addtask prepare_copyleft_sources after do_fetch before do_build
do_prepare_copyleft_sources[dirs] = "${WORKDIR}"
do_build[recrdeptask] += 'do_prepare_copyleft_sources'
