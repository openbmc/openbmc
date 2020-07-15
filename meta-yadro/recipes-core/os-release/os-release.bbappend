# This recipe append assumes that all YADRO branches follow the
# <type>/<product>/<description> convention, or are either
# "merge/upstream" or "master" e.g.:
#
# release/nicole/v1
# feature/vesnin/some-feature
# merge/upstream
# master
#
# Release branches (release/<machine>/vX) are expected to be forked off
# the master branch and may contain tags in the form
# <machine>-vX.Y[-rcZ|-dev*]. All release branches without machine tags,
# as well as any non-release branches produce 'Unofficial' builds.
# So do the release branches with -rc or -dev suffix in the latest tag.

python do_compile_prepend() {
    print ("Preparing YADRO-specific version information")
    version_id = d.getVar('VERSION_ID')

    print ('Original VERSION_ID = %s' % version_id)

    versionList = version_id.split('-')

    branch_info = run_git(d, 'rev-parse --abbrev-ref HEAD').split('/')
    branch_type = branch_info[0]

    if len(branch_info) > 1:
        branch_product = branch_info[1]
    else:
        branch_product = d.getVar('MACHINE', True).split('-')[0]

    if len(branch_info) > 2:
        # This is for <type>/<product>/<description> branches
        # and <type>/<product>/<any>/<level>/<of>/<hierarchy> branches alike
        branch_name = '-'.join(branch_info[2::])
    else:
        # This is for "merge/upstream", "master" and any arbitrary branches
        branch_name = '-'.join(branch_info)

    print ('Branch type = %s' % branch_type)
    print ('Branch product = %s' % branch_product)
    print ('Branch name = %s' % branch_name)

    # For <product>-vX.Y tags, simply strip off the '<product>-' part,
    # then pretend it is a normal version tag
    product_tagged = False
    if branch_product == versionList[0]:
        product_tagged = True
        versionList.pop(0)

    version = versionList[0] if len(versionList) > 0 else ''
    if versionList[1][:2] == 'rc' or versionList[1] == 'dev': # Remove the '-rcX' and '-dev' parts
        rcdev = versionList[1]
        versionList.pop(1)
    patch_level = versionList[1] if len(versionList) > 1 else 0
    git_hash = versionList[2] if len(versionList) > 2 else 'nongit'
    dirty = ('dirty' == versionList[3]) if len(versionList) > 3 else 0

    # For release branches:
    if 'release' == branch_type:
        flag = ''
        if not product_tagged:
            # If there is no tag, take branch name for the major version
            # and assume the minor version to be 0, patch level will
            # represent the number of commits since branch creation
            # (assuming that it branched off the master branch)
            patch_level = run_git(d, 'rev-list --count origin/master..%s'
                                     % '/'.join(branch_info))
            # Prevent zero patch level. Zero patch level is an official release.
            patch_level = str(int(patch_level) + 1)
            version = branch_name.split('-')[0]
            version += '.0'
            # Any build from a release/<product>/* branch without a <product>-* tag
            # is not an official release
            release = 'Unofficial ' + branch_name
            flag = '-unofficial'
        else:
            # If there is a product tag, then it is used as the normal tag to
            # designate the major and minor version, patch level is as usual
            # the number of commits since the tag.
            release = version
            if (rcdev):
                flag = '-' + rcdev
                release += flag
            # Official releases happen only exactly on tags, without extra
            # commits. Release candidates and development releases are also
            # not considered 'official'
            if int(patch_level) or rcdev:
                release = 'Unofficial ' + release
                flag += '-unofficial'

        version_id = version
        version_id += 'r' + git_hash[1:7]
        version_id += ('p' + patch_level) if int(patch_level) else ''
        version_id += flag
        version_id += '-dirty' if dirty else ''
        name = 'YADRO %s BMC Firmware'
    else:
        version_id += ("-" + branch_name) if (branch_name) else ''
        release = version_id
        name = 'YADRO %s BMC Development Firmware'

    u_product = branch_product.upper()

    d.setVar('VERSION_ID', version_id)
    d.setVar('VERSION', version)
    d.setVar('RELEASE', release)
    d.setVar('PATCH_LEVEL', patch_level)
    d.setVar('NAME', '%s' % (name % u_product))
    d.setVar('PRETTY_NAME', '%s %s' % (name % u_product, release))

    print ('%s VERSION_ID = %s' % (u_product, version_id))
    print ('%s RELEASE = %s' % (u_product, release))
    print ('%s PATCH_LEVEL = %s' % (u_product, patch_level))
}

python do_compile_append () {
    with open(d.expand('${B}/issue'), 'w') as f:
        f.write('%s %s @ \\l\n' % (name % u_product, release))
}

do_install_append () {
    install -m 0644 issue ${D}${sysconfdir}/issue
    install -m 0644 issue ${D}${sysconfdir}/issue.net
}

CONFFILES_${PN} += " ${sysconfdir}/issue ${sysconfdir}/issue.net"
OS_RELEASE_FIELDS_append = " RELEASE PATCH_LEVEL"
BB_DONT_CACHE = "1"
