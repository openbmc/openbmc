#
# Copyright 2023 (C) Weidmueller GmbH & Co KG
# Author: Lukas Funke <lukas.funke@weidmueller.com>
#
# Handle Go vendor support for offline builds
#
# When importing Go modules, Go downloads the imported modules using
# a network (proxy) connection ahead of the compile stage. This contradicts 
# the yocto build concept of fetching every source ahead of build-time
# and supporting offline builds.
#
# To support offline builds, we use Go 'vendoring': module dependencies are 
# downloaded during the fetch-phase and unpacked into the modules 'vendor'
# folder. Additionally a manifest file is generated for the 'vendor' folder
# 

inherit go-mod

def go_src_uri(repo, version, path=None, subdir=None, \
                vcs='git', replaces=None, pathmajor=None):

    destsuffix = "git/src/import/vendor.fetch"
    module_path = repo if not path else path

    src_uri = "{}://{};name={}".format(vcs, repo, module_path.replace('/', '.'))
    src_uri += ";destsuffix={}/{}@{}".format(destsuffix, repo, version)

    if vcs == "git":
        src_uri += ";nobranch=1;protocol=https"

    src_uri += ";go_module_path={}".format(module_path)

    if replaces:
        src_uri += ";go_module_replacement={}".format(replaces)
    if subdir:
        src_uri += ";go_subdir={}".format(subdir)
    if pathmajor:
        src_uri += ";go_pathmajor={}".format(pathmajor)
    src_uri += ";is_go_dependency=1"

    return src_uri

python do_vendor_unlink() {
    go_import = d.getVar('GO_IMPORT')
    source_dir = d.getVar('S')
    linkname = os.path.join(source_dir, *['src', go_import, 'vendor'])

    os.unlink(linkname)
}

addtask vendor_unlink before do_package after do_install

python do_go_vendor() {
    import shutil

    src_uri = (d.getVar('SRC_URI') or "").split()

    if not src_uri:
        bb.fatal("SRC_URI is empty")

    default_destsuffix = "git/src/import/vendor.fetch"
    fetcher = bb.fetch2.Fetch(src_uri, d)
    go_import = d.getVar('GO_IMPORT')
    source_dir = d.getVar('S')

    linkname = os.path.join(source_dir, *['src', go_import, 'vendor'])
    vendor_dir = os.path.join(source_dir, *['src', 'import', 'vendor'])
    import_dir = os.path.join(source_dir, *['src', 'import', 'vendor.fetch'])

    if os.path.exists(vendor_dir):
        # Nothing to do except re-establish link to actual vendor folder
        if not os.path.exists(linkname):
            os.symlink(vendor_dir, linkname)
        return

    bb.utils.mkdirhier(vendor_dir)

    modules = {}

    for url in fetcher.urls:
        srcuri = fetcher.ud[url].host + fetcher.ud[url].path

        # Skip non Go module src uris
        if not fetcher.ud[url].parm.get('is_go_dependency'):
            continue

        destsuffix = fetcher.ud[url].parm.get('destsuffix')
        # We derive the module repo / version in the following manner (exmaple):
        # 
        # destsuffix = git/src/import/vendor.fetch/github.com/foo/bar@v1.2.3
        # p = github.com/foo/bar@v1.2.3
        # repo = github.com/foo/bar
        # version = v1.2.3

        p = destsuffix[len(default_destsuffix)+1:]
        repo, version = p.split('@')

        module_path = fetcher.ud[url].parm.get('go_module_path')

        subdir = fetcher.ud[url].parm.get('go_subdir')
        subdir = None if not subdir else subdir

        pathMajor = fetcher.ud[url].parm.get('go_pathmajor')
        pathMajor = None if not pathMajor else pathMajor.strip('/')

        if not (repo, version) in modules:
            modules[(repo, version)] =   {
                                "repo_path": os.path.join(import_dir, p),
                                "module_path": module_path,
                                "subdir": subdir,
                                "pathMajor": pathMajor }

    for module_key, module in modules.items():

        # only take the version which is explicitly listed
        # as a dependency in the go.mod
        module_path = module['module_path']
        rootdir = module['repo_path']
        subdir = module['subdir']
        pathMajor = module['pathMajor']

        src = rootdir

        if subdir:
            src = os.path.join(rootdir, subdir)

        # If the module is released at major version 2 or higher, the module
        # path must end with a major version suffix like /v2.
        # This may or may not be part of the subdirectory name
        #
        # https://go.dev/ref/mod#modules-overview
        if pathMajor:
            tmp = os.path.join(src, pathMajor)
            # source directory including major version path may or may not exist
            if os.path.exists(tmp):
                src = tmp

        dst = os.path.join(vendor_dir, module_path)

        bb.debug(1, "cp %s --> %s" % (src, dst))
        shutil.copytree(src, dst, symlinks=True, dirs_exist_ok=True, \
            ignore=shutil.ignore_patterns(".git", \
                                            "vendor", \
                                            "*._test.go"))

        # If the root directory has a LICENSE file but not the subdir
        # we copy the root license to the sub module since the license
        # applies to all modules in the repository
        # see https://go.dev/ref/mod#vcs-license
        if subdir:
            rootdirLicese = os.path.join(rootdir, "LICENSE")
            subdirLicense = os.path.join(src, "LICENSE")

            if not os.path.exists(subdir) and \
                os.path.exists(rootdirLicese):
                shutil.copy2(rootdirLicese, subdirLicense)

    # Copy vendor manifest
    modules_txt_src = os.path.join(d.getVar('UNPACKDIR'), "modules.txt")
    bb.debug(1, "cp %s --> %s" % (modules_txt_src, vendor_dir))
    shutil.copy2(modules_txt_src, vendor_dir)

    # Clean up vendor dir
    # We only require the modules in the modules_txt file
    fetched_paths = set([os.path.relpath(x[0], vendor_dir) for x in os.walk(vendor_dir)])

    # Remove toplevel dir
    fetched_paths.remove('.')

    vendored_paths = set()
    replaced_paths = dict()
    with open(modules_txt_src) as f:
        for line in f:
            if not line.startswith("#"):
                line = line.strip()
                vendored_paths.add(line)

                # Add toplevel dirs into vendored dir, as we want to keep them
                topdir = os.path.dirname(line)
                while len(topdir):
                    if not topdir in vendored_paths:
                        vendored_paths.add(topdir)

                    topdir = os.path.dirname(topdir)
            else:
                replaced_module = line.split("=>")
                if len(replaced_module) > 1:
                    # This module has been replaced, use a local path
                    # we parse the line that has a pattern "# module-name [module-version] => local-path
                    actual_path = replaced_module[1].strip()
                    vendored_name = replaced_module[0].split()[1]
                    bb.debug(1, "added vendored name %s for actual path %s" % (vendored_name, actual_path))
                    replaced_paths[vendored_name] = actual_path

    for path in fetched_paths:
        if path not in vendored_paths:
            realpath = os.path.join(vendor_dir, path)
            if os.path.exists(realpath):
                shutil.rmtree(realpath)

    for vendored_name, replaced_path in replaced_paths.items():
        symlink_target = os.path.join(source_dir, *['src', go_import, replaced_path])
        symlink_name = os.path.join(vendor_dir, vendored_name)
        bb.debug(1, "vendored name %s, symlink name %s" % (vendored_name, symlink_name))
        os.symlink(symlink_target, symlink_name)

    # Create a symlink to the actual directory
    os.symlink(vendor_dir, linkname)
}

addtask go_vendor before do_patch after do_unpack
