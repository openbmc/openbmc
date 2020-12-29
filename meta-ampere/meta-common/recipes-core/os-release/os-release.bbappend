def run_git(d, cmd):
    try:
        oeroot = d.getVar('COREBASE', True)
        return bb.process.run("git --work-tree %s --git-dir %s/.git %s"
            % (oeroot, oeroot, cmd))[0].strip('\n')
    except:
        pass

python() {
    version_id = run_git(d, 'describe --dirty --long --tags')
    if version_id:
        versionList = version_id.split('-')

        # Override with tag name only, if built at tag.
        if 'dirty' not in version_id and versionList[1] == 'ampere':
            d.setVar('VERSION_ID', versionList[0].split('v')[-1])
}
