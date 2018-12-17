# Gather existing and candidate postinst intercepts from BBPATH
POSTINST_INTERCEPTS_DIR ?= "${COREBASE}/scripts/postinst-intercepts"
POSTINST_INTERCEPTS_PATHS ?= "${@':'.join('%s/postinst-intercepts' % p for p in '${BBPATH}'.split(':'))}:${POSTINST_INTERCEPTS_DIR}"

python find_intercepts() {
    intercepts = {}
    search_paths = []
    paths = d.getVar('POSTINST_INTERCEPTS_PATHS').split(':')
    overrides = (':' + d.getVar('FILESOVERRIDES')).split(':') + ['']
    search_paths = [os.path.join(p, op) for p in paths for op in overrides]
    searched = oe.path.which_wild('*', ':'.join(search_paths), candidates=True)
    files, chksums = [], []
    for pathname, candidates in searched:
        if os.path.isfile(pathname):
            files.append(pathname)
            chksums.append('%s:True' % pathname)
            chksums.extend('%s:False' % c for c in candidates[:-1])

    d.setVar('POSTINST_INTERCEPT_CHECKSUMS', ' '.join(chksums))
    d.setVar('POSTINST_INTERCEPTS', ' '.join(files))
}
find_intercepts[eventmask] += "bb.event.RecipePreFinalise"
addhandler find_intercepts
