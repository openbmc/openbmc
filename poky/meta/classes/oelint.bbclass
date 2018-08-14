addtask lint before do_build
do_lint[nostamp] = "1"
python do_lint() {
    pkgname = d.getVar("PN")

    ##############################
    # Test that DESCRIPTION exists
    #
    description = d.getVar("DESCRIPTION", False)
    if description[1:10] == '{SUMMARY}':
        bb.warn("%s: DESCRIPTION is not set" % pkgname)


    ##############################
    # Test that HOMEPAGE exists
    #
    homepage = d.getVar("HOMEPAGE", False)
    if homepage == '':
        bb.warn("%s: HOMEPAGE is not set" % pkgname)
    elif not homepage.startswith("http://") and not homepage.startswith("https://"):
        bb.warn("%s: HOMEPAGE doesn't start with http:// or https://" % pkgname)


    ##############################
    # Test for valid SECTION
    #
    section = d.getVar("SECTION", False)
    if section == '':
        bb.warn("%s: SECTION is not set" % pkgname)
    elif not section.islower():
        bb.warn("%s: SECTION should only use lower case" % pkgname)


    ##############################
    # Check that all patches have Signed-off-by and Upstream-Status
    #
    srcuri = d.getVar("SRC_URI", False).split()
    fpaths = (d.getVar('FILESPATH') or '').split(':')

    def findPatch(patchname):
        for dir in fpaths:
            patchpath = dir + patchname
            if os.path.exists(patchpath):
                 return patchpath

    def findKey(path, key):
        ret = True
        f = open('%s' % path, mode = 'r')
        line = f.readline()
        while line:
            if line.find(key) != -1:
                ret = False
            line = f.readline()
        f.close()
        return ret

    def checkPN(pkgname, varname, str):
        if str.find("{PN}") != -1:
            bb.warn("%s: should use BPN instead of PN in %s" % (pkgname, varname))
        if str.find("{P}") != -1:
            bb.warn("%s: should use BP instead of P in %s" % (pkgname, varname))

    length = len("file://")
    for item in srcuri:
        if item.startswith("file://"):
            item = item[length:]
            if item.endswith(".patch") or item.endswith(".diff"):
                path = findPatch(item)
                if findKey(path, "Signed-off-by"):
                    bb.warn("%s: %s doesn't have Signed-off-by" % (pkgname, item))
                if findKey(path, "Upstream-Status"):
                    bb.warn("%s: %s doesn't have Upstream-Status" % (pkgname, item))


    ##############################
    # Check for ${PN} or ${P} usage in SRC_URI or S
    # Should use ${BPN} or ${BP} instead to avoid breaking multilib
    #
    for s in srcuri:
        if not s.startswith("file://"):
            checkPN(pkgname, 'SRC_URI', s)

    checkPN(pkgname, 'S', d.getVar('S', False))
}
